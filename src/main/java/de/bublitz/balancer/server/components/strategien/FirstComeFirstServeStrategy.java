package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
public class FirstComeFirstServeStrategy extends Strategy {
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
            anschluss.computeLoad();
        }
    }

    @Override
    public void addLV(ChargeBox chargeBox) throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        if (anschlussLoad <= anschluss.getHardLimit()) {
            chargingList.add(chargeBox);
        } else {
            stopWithPenalty();
            while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty()) {
                ChargeBox ln = chargingList.getLast();
                chargingList.remove(ln);
                anschlussLoad = anschlussLoad - ln.getCurrentLoad();
                tmpSuspendedList.add(ln); // Stop later
            }
            calculateFitting(anschlussLoad);
            // Penalty erhöhen
            chargingList.add(chargeBox);
            // Chargingbox hat verbraucht zu viel
            if (anschlussLoad > anschluss.getHardLimit()) {
                chargeBox.setCurrentLoad(0);
                suspendedList.add(chargeBox);
                chargingList.remove(chargeBox);
                chargingList.addAll(tmpSuspendedList);
                tmpSuspendedList.clear();
            }
            penaltyMap.replaceAll((cb, p) -> p + 1);
        }
        for (ChargeBox box : tmpSuspendedList) {
            stop(box);
        }
        suspendedList.addAll(tmpSuspendedList);
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
        tmpSuspendedList.clear();
        anschluss.computeLoad();
    }

    private void stopWithPenalty() {
        // Alle LV mit penalty p > p auf tmpSuspended packen!
        penaltyMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= penalty)
                .forEach(entry -> {
                    log.info(entry.getKey() + " can be stopped (Reason: penalty)!");
                    ChargeBox chargeBox = chargingList.stream().filter(cb -> cb.getEvseid().equals(entry.getKey())).findFirst().get();
                    chargingList.remove(chargeBox);
                    tmpSuspendedList.add(chargeBox);
                    anschlussLoad -= chargeBox.getCurrentLoad();
                });
    }

    @Override
    public void removeLV(ChargeBox chargeBox) throws NotStoppedException {
        if (chargingList.contains(chargeBox)) {
            chargingList.remove(chargeBox);
        } else {
            suspendedList.remove(chargeBox);
        }
        penaltyMap.remove(chargeBox.getEvseid());
        optimize();
    }
}
