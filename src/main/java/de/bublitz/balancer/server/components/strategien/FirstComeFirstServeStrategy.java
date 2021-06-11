package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class FirstComeFirstServeStrategy extends Strategy {
    private final List<ChargeBox> stoppedDuePenalty = new LinkedList<>();
    private final Map<String, Integer> penaltyMap = new LinkedHashMap<>();
    @Getter
    @Setter
    private int penalty = 3;

    public FirstComeFirstServeStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.FCFS);
    }

    @Override
    public void optimize() throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        if (!suspendedList.isEmpty()) {
            ChargeBox u0 = suspendedList.getFirst();
            suspendedList.remove(u0);
            u0.setCurrentLoad(u0.getLastLoad());
            addLV(u0);
            // starte den LV
            if (chargingList.contains(u0)) {
                start(u0);
            }
        } else {
            decreaseLoad();
        }
    }

    @Override
    public void addLV(ChargeBox chargeBox) throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        if (anschlussLoad <= anschluss.getHardLimit()) {
            chargingList.add(chargeBox);
            calculateFitting(anschlussLoad);
        } else {
            stopWithPenalty();

            // Penalty erhöhen
            if (suspendedList.isEmpty()) {
                chargingList.add(chargeBox);
                // Chargingbox hat verbraucht zu viel
                revertStartingIfNeeded(chargeBox);
            } else {
                if (chargeBox.isCharging()) {
                    suspendedList.add(chargeBox);
                } else {
                    suspendedList.addFirst(chargeBox);
                }
                anschlussLoad -= chargeBox.getCurrentLoad();
                stop(chargeBox);
            }
            decreaseLoad();

        }
        penaltyMap.replaceAll((cb, p) -> p + 1);
        suspendedList.forEach(cb -> {
            // Ladevorgang wurde beendet -> Keine Penalty mehr
            penaltyMap.remove(cb.getEvseid());
        });
        // Hinzufügen zur penaltyMap
        chargingList.forEach(cb -> {
            if (!penaltyMap.containsKey(cb.getEvseid())) {
                penaltyMap.put(cb.getEvseid(), 1);
            }
        });
        anschlussLoad = anschluss.getCurrentLoad();
    }

    private boolean stopWithPenalty() {
        // Alle LV mit penalty p > p auf tmpSuspended packen!
        AtomicReference<Boolean> hasPenalty = new AtomicReference<>(false);
        penaltyMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= penalty)
                .forEach(entry -> {
                    log.debug(entry.getKey() + " will be stopped (Reason: penalty)!");
                    ChargeBox chargeBox = chargingList.stream().filter(cb -> cb.getEvseid().equals(entry.getKey())).findFirst().get();
                    chargingList.remove(chargeBox);
                    suspendedList.add(chargeBox);
                    stoppedDuePenalty.add(chargeBox);
                    anschlussLoad -= chargeBox.getCurrentLoad();
                    hasPenalty.set(true);
                    try {
                        stop(chargeBox);
                    } catch (NotStoppedException e) {
                        log.error(e.getMessage());
                    }

                });
        return hasPenalty.get();
    }

    @Override
    public void removeLV(ChargeBox chargeBox) {
        if (chargingList.contains(chargeBox)) {
            chargingList.remove(chargeBox);
        } else {
            suspendedList.remove(chargeBox);
        }
        penaltyMap.remove(chargeBox.getEvseid());
    }

    private void decreaseLoad() throws NotStoppedException {
        while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty()) {
            // Anschlusslast verringern!
            ChargeBox ln = chargingList.getLast();
            chargingList.remove(ln);
            anschlussLoad -= ln.getCurrentLoad();
            tmpSuspendedList.add(ln); // Stop later
        }
        // Gibt's evtl. Restkapazitäten? -> falls ja LV starten
        calculateFitting(anschlussLoad);
        for (ChargeBox chargeBox : tmpSuspendedList) {
            stop(chargeBox);
        }
        suspendedList.addAll(tmpSuspendedList);
        tmpSuspendedList.clear();
        stoppedDuePenalty.forEach(cb -> {
            if (suspendedList.contains(cb)) {
                suspendedList.remove(cb);
                suspendedList.add(cb);
            }
        });
        stoppedDuePenalty.clear();
        anschlussLoad = anschluss.getCurrentLoad();
    }
}
