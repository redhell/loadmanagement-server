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
            start(u0);
        } else {
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
            anschluss.computeLoad();
        }
    }

    @Override
    public void addLV(ChargeBox chargeBox) throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        if (anschlussLoad <= anschluss.getHardLimit()) {
            chargingList.add(chargeBox);
        } else {
            while (anschlussLoad > anschluss.getHardLimit()) {
                ChargeBox l0 = chargingList.getFirst();
                chargingList.remove(l0);
                anschlussLoad -= l0.getCurrentLoad();
                tmpSuspendedList.add(l0); // Stop later
            }
            calculateFitting(anschlussLoad);
            chargingList.add(chargeBox);
            //start(chargeBox);
            //anschlussLoad += cbLoad;
        }
        for (ChargeBox cb : tmpSuspendedList) {
            stop(cb);
        }
        suspendedList.addAll(tmpSuspendedList);
        tmpSuspendedList.clear();
        anschluss.computeLoad();
    }
}
