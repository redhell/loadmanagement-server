package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class FirstComeFirstServeStrategy extends Strategy {
    private final Map<ChargeBox, Integer> penaltyMap = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private int penalty = 3;

    public FirstComeFirstServeStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.FCFS);
    }

    @Override
    public void optimize() throws NotStoppedException {
        //anschlussLoad = anschluss.getCurrentLoad();
        if (!getSuspended().isEmpty()) {
            ChargeBox u0 = suspended.getFirst();
            suspended.remove(u0);
            addLV(u0);
        } else {
            int tries = 0;
            while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty() && tries <= 5) {
                ChargeBox l0 = chargingList.getFirst();
                double l0_Load = l0.getCurrentLoad();
                tries++;
                if (stop(l0)) {
                    chargingList.remove(l0);
                    anschlussLoad -= l0_Load;
                    suspended.add(l0); // Stop later
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
        double cbLoad;
        if (chargeBox.getCurrentLoad() > chargeBox.getIdleConsumption()) {
            cbLoad = chargeBox.getCurrentLoad();
        } else {
            cbLoad = chargeBox.getLastLoad();
        }
        if (anschlussLoad + cbLoad <= anschluss.getHardLimit()) {
            chargingList.add(chargeBox);
        } else {
            stopWithPenalty();
            while (anschlussLoad + cbLoad > anschluss.getHardLimit()) {
                ChargeBox ln = chargingList.getLast();
                chargingList.remove(ln);
                anschlussLoad -= ln.getCurrentLoad();
                tmpSuspended.add(ln); // Stop later
            }
            chargingList.add(chargeBox);
            start(chargeBox);
            anschlussLoad += cbLoad;
            calculateFitting(anschlussLoad);
            // Penalty erhöhen
            penaltyMap.forEach((cb, p) -> penaltyMap.merge(cb, 1, Integer::sum));
        }
        suspended.addAll(tmpSuspended);
        suspended.forEach(cb -> {
            if (cb.getCurrentLoad() > 0) {
                cb.setLastLoad(cb.getCurrentLoad());
            }
            stop(cb);
            // Ladevorgang wurde beendet -> Keine Penalty mehr
            penaltyMap.remove(cb);
        });
        // Hinzufügen zur penaltyMap
        chargingList.forEach(cb -> {
            if (!penaltyMap.containsKey(cb)) {
                penaltyMap.put(cb, 0);
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
                    chargingList.remove(entry.getKey());
                    tmpSuspended.add(entry.getKey());
                    anschlussLoad -= entry.getKey().getCurrentLoad();
                });
    }
}
