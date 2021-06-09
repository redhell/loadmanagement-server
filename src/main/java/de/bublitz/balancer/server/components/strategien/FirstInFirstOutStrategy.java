package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
// Round robin
public class FirstInFirstOutStrategy extends Strategy {

    public FirstInFirstOutStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.FIFO);
    }

    @Override
    public void optimize() throws NotStoppedException {
        if (!suspendedList.isEmpty()) {
            // Es gibt unterbrochene Ladevorgänge
            ChargeBox u0 = suspendedList.getFirst();
            suspendedList.remove(u0);
            // Entferne u0 von der suspendedList
            u0.setCurrentLoad(u0.getLastLoad());
            // Füge u0 in den Schedule ein mit der alten Last
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
            while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty()) {
                ChargeBox l0 = chargingList.getFirst();
                chargingList.remove(l0);
                anschlussLoad -= l0.getCurrentLoad();
                tmpSuspendedList.add(l0); // Stop later
            }

            chargingList.add(chargeBox);
            // Chargingbox hat verbraucht zu viel
            revertStartingIfNeeded(chargeBox);

            // Falls Last immer noch zu hoch
            decreaseLoad();
        }
    }


    private void decreaseLoad() throws NotStoppedException {
        while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty()) {
            // Anschlusslast verringern!
            ChargeBox l0 = chargingList.getFirst();
            chargingList.remove(l0);
            anschlussLoad -= l0.getCurrentLoad();
            tmpSuspendedList.add(l0); // Stop later
        }

        // Gibt's evtl. Restkapazitäten? -> falls ja LV starten
        calculateFitting(anschlussLoad);

        for (ChargeBox chargeBox : tmpSuspendedList) {
            stop(chargeBox);
        }
        suspendedList.addAll(tmpSuspendedList);
        tmpSuspendedList.clear();
        anschlussLoad = anschluss.getCurrentLoad();
    }
}
