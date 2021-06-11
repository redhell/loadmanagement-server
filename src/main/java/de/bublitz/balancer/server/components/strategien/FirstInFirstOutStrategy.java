package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.extern.log4j.Log4j2;

// Round robin
@Log4j2
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
            decreaseLoad();

            chargingList.add(chargeBox);
            // Chargingbox hat verbraucht zu viel
            if (revertStartingIfNeeded(chargeBox)) {
                if (calculateFitting(anschlussLoad - chargingList.getFirst().getCurrentLoad())) {
                    ChargeBox l0 = chargingList.removeFirst();
                    tmpSuspendedList.add(l0);
                    anschlussLoad = anschluss.getCurrentLoad() - l0.getCurrentLoad();
                }
            }

            // Falls Last immer noch zu hoch
            decreaseLoad();

            // Gibt's evtl. Restkapazitäten? -> falls ja LV starten
            calculateFitting(anschlussLoad);

            for (ChargeBox stoppable : tmpSuspendedList) {
                stop(stoppable);
                log.debug(stoppable.getName() + " will be stopped");
            }
            suspendedList.addAll(tmpSuspendedList);
            tmpSuspendedList.clear();
            anschlussLoad = anschluss.getCurrentLoad();
        }
    }


    private void decreaseLoad() {
        while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty()) {
            // Anschlusslast verringern!
            ChargeBox l0 = chargingList.getFirst();
            chargingList.remove(l0);
            anschlussLoad -= l0.getCurrentLoad();
            tmpSuspendedList.add(l0); // Stop later
            log.debug(l0.getName() + " maybe stopped");
        }
    }
}
