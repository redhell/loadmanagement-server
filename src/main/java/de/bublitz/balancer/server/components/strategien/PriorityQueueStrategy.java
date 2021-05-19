package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Beendet Ladevorgang wenn MAX Last vom Anschluss erreicht bzw am limit ist
 */
public class PriorityQueueStrategy extends Strategy {

    public PriorityQueueStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.PQ);
    }

    @Override
    public void optimize() throws NotStoppedException {
        //anschlussLoad = anschluss.getCurrentLoad();
        if (!suspendedList.isEmpty()) {
            ChargeBox u0;
            // Priority Box in der Warteschlange?
            Optional<ChargeBox> priorityBox = suspendedList.stream().filter(ChargeBox::isPriority).findFirst();
            u0 = priorityBox.orElseGet(() -> suspendedList.getFirst());
            suspendedList.remove(u0);
            u0.setCurrentLoad(u0.getLastLoad());
            addLV(u0);
            start(u0);
        } else {
            stopNonPrio();
            stopPrio();
            anschluss.computeLoad();
            calculateFitting(anschlussLoad);
        }
    }

    private void stopNonPrio() throws NotStoppedException {
        List<ChargeBox> chargeBoxList = chargingList.stream().filter(cb -> !cb.isPriority()).collect(Collectors.toList());
        stopAndRemove(new LinkedList<>(chargeBoxList));
    }

    private void stopPrio() throws NotStoppedException {
        List<ChargeBox> chargeBoxList = chargingList.stream().filter(ChargeBox::isPriority).collect(Collectors.toList());
        stopAndRemove(new LinkedList<>(chargeBoxList));
    }

    private void stopAndRemove(LinkedList<ChargeBox> tmpChargingList) throws NotStoppedException {
        int tries = 0;
        while (anschlussLoad > anschluss.getHardLimit() && !chargingList.isEmpty() && tries <= 5) {
            ChargeBox l0 = tmpChargingList.getFirst();
            double l0_Load = l0.getCurrentLoad();
            tries++;
            if (stop(l0)) {
                chargingList.remove(l0);
                tmpChargingList.remove(l0);
                anschlussLoad -= l0_Load;
                suspendedList.add(l0);
                tries = 0;
            }
            if (tries == 5)
                throw new NotStoppedException();
        }
    }

    @Override
    public void addLV(ChargeBox chargeBox) {
        anschlussLoad = anschluss.getCurrentLoad();
        if (anschlussLoad <= anschluss.getHardLimit()) {
            chargingList.add(chargeBox);
        } else {
            int i = 0;
            // Anschlussload beinhaltet schon cbLoad!
            while (anschlussLoad > anschluss.getHardLimit()) {
                ChargeBox l0 = chargingList.get(i);
                if (!l0.isPriority()) {
                    chargingList.remove(l0);
                    anschlussLoad -= l0.getCurrentLoad();
                    tmpSuspendedList.add(l0); // Stop later
                    i = 0;
                } else {
                    i++;
                }
            }
            calculateFitting(anschlussLoad);
            chargingList.add(chargeBox);
        }
        tmpSuspendedList.forEach(this::stop);
        suspendedList.addAll(tmpSuspendedList);
        tmpSuspendedList.clear();
        anschluss.computeLoad();
    }
}
