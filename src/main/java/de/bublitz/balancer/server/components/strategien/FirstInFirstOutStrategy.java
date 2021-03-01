package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;

public class FirstInFirstOutStrategy extends Strategy {

    public FirstInFirstOutStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.FIFO);
    }

    @Override
    public void optimize() {
        anschlussLoad = anschluss.getCurrentLoad();
        if (!getSuspended().isEmpty()) {
            ChargeBox u0 = suspended.getFirst();
            suspended.remove(u0);
            addLV(u0);
        } else {
            while (anschlussLoad > anschluss.getHardLimit()) {
                ChargeBox l0 = chargingList.getFirst();
                chargingList.remove(l0);
                anschlussLoad -= l0.getCurrentLoad();
                suspended.add(l0); // Stop later
                stop(l0);
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
        if (anschlussLoad + cbLoad <= anschluss.getMaxLoad()) {
            chargingList.add(chargeBox);
        } else {
            while (anschlussLoad + cbLoad > anschluss.getMaxLoad()) {
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
