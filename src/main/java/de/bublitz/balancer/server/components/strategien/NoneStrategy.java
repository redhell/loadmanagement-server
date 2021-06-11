package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;

public class NoneStrategy extends Strategy {

    public NoneStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.NONE);
    }

    @Override
    public void optimize() throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        double lastLoad = 0;
        while (!suspendedList.isEmpty() && anschlussLoad <= anschluss.getHardLimit()) {
            ChargeBox u0 = suspendedList.getFirst();
            suspendedList.remove(u0);
            u0.setCurrentLoad(u0.getLastLoad());
            addLV(u0);
            // starte den LV
            if (chargingList.contains(u0)) {
                start(u0);
            }
            if (lastLoad == anschlussLoad)
                break;
            lastLoad = anschlussLoad;
        }
    }

    @Override
    public void addLV(ChargeBox chargeBox) throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        if (anschlussLoad <= anschluss.getHardLimit()) {
            add(chargeBox);
        } else {
            // Nichts machen hinten anstellen.
            chargingList.add(chargeBox);

            while (anschlussLoad > anschluss.getHardLimit()) {
                ChargeBox lastCB = chargingList.getLast();
                anschlussLoad -= lastCB.getCurrentLoad();
                chargingList.remove(lastCB);

                if (lastCB.isCharging()) {
                    stop(lastCB);
                    suspendedList.add(lastCB);
                } else {
                    lastCB.setLastLoad(lastCB.getCurrentLoad());
                    lastCB.setCurrentLoad(0);
                    suspendedList.addFirst(lastCB);
                }
            }
        }
        anschlussLoad = anschluss.getCurrentLoad();
    }
}
