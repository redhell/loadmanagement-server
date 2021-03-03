package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;

public class FirstInFirstOutStrategy extends Strategy {

    public FirstInFirstOutStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.FIFO);
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
            while (anschlussLoad + cbLoad > anschluss.getHardLimit()) {
                ChargeBox l0 = chargingList.getFirst();
                chargingList.remove(l0);
                anschlussLoad -= l0.getCurrentLoad();
                tmpSuspended.add(l0); // Stop later
            }
            chargingList.add(chargeBox);
            start(chargeBox);
            anschlussLoad += cbLoad;
            calculateFitting(anschlussLoad);

        }
        suspended.addAll(tmpSuspended);
        suspended.forEach(cb -> {
            if (cb.getCurrentLoad() > 0) {
                cb.setLastLoad(cb.getCurrentLoad());
            }
            stop(cb);
        });
        tmpSuspended.clear();
        anschluss.computeLoad();
    }
}
