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
        if (!getSuspended().isEmpty()) {
            ChargeBox u0 = suspended.getFirst();
            suspended.remove(u0);
            u0.setCurrentLoad(u0.getLastLoad());
            addLV(u0);
            start(u0);
        } else {
            int tries = 0;
            while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty() && tries <= 5) {
                ChargeBox ln = chargingList.getLast();
                double ln_Load = ln.getCurrentLoad();
                tries++;
                if (stop(ln)) {
                    chargingList.remove(ln);
                    anschlussLoad -= ln_Load;
                    suspended.add(ln);
                    tries = 0;
                }
                if (tries == 5)
                    throw new NotStoppedException();
            }
            anschluss.computeLoad();
        }
    }

    @Override
    public void addLV(ChargeBox chargeBox) {
        anschlussLoad = anschluss.getCurrentLoad();
        if (anschlussLoad <= anschluss.getHardLimit()) {
            chargingList.add(chargeBox);
        } else {
            stopWithPenalty();
            while (anschlussLoad > anschluss.getHardLimit()) {
                ChargeBox ln = chargingList.getLast();
                chargingList.remove(ln);
                anschlussLoad = anschlussLoad - ln.getCurrentLoad();
                tmpSuspended.add(ln); // Stop later
            }
            //anschlussLoad = anschlussLoad + cbLoad; // Warum?
            calculateFitting(anschlussLoad);
            // Penalty erhöhen
            chargingList.add(chargeBox);
            penaltyMap.replaceAll((cb, p) -> p + 1);
        }
        tmpSuspended.forEach(this::stop);
        suspended.addAll(tmpSuspended);
        suspended.forEach(cb -> {
            // Ladevorgang wurde beendet -> Keine Penalty mehr
            penaltyMap.remove(cb.getEvseid());
        });
        // Hinzufügen zur penaltyMap
        chargingList.forEach(cb -> {
            if (!penaltyMap.containsKey(cb.getEvseid())) {
                penaltyMap.put(cb.getEvseid(), 0);
            }
        });
        tmpSuspended.clear();
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
                    tmpSuspended.add(chargeBox);
                    anschlussLoad -= chargeBox.getCurrentLoad();
                });
    }

    @Override
    public void removeLV(ChargeBox chargeBox) {
        if (chargingList.contains(chargeBox)) {
            chargingList.remove(chargeBox);
        } else {
            suspended.remove(chargeBox);
        }
        penaltyMap.remove(chargeBox.getEvseid());
    }
}
