package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Optional;

/**
 * Beendet Ladevorgang wenn MAX Last vom Anschluss erreicht bzw am limit ist
 */
public class PriorityQueueStrategy extends Strategy {

    public PriorityQueueStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.PQ);
    }

    @Getter
    private LinkedList<ChargeBox> priorityQueue = new LinkedList<>();

    @Override
    public void optimize() throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        if (!suspendedList.isEmpty()) {
            ChargeBox u0;
            // Priority Box in der Warteschlange?
            Optional<ChargeBox> priorityBox = suspendedList.stream().filter(ChargeBox::isPriority).findFirst();
            u0 = priorityBox.orElseGet(() -> suspendedList.getFirst());
            suspendedList.remove(u0);
            u0.setCurrentLoad(u0.getLastLoad());
            addLV(u0);
            if (chargingList.contains(u0) || priorityQueue.contains(u0)) {
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
            add(chargeBox);
            calculateFitting(anschlussLoad);
        } else {
            while (anschlussLoad > anschluss.getHardLimit()) {
                // Keine non-Prio LV mehr, entferne Prio
                if (chargingList.isEmpty() && !priorityQueue.isEmpty() && chargeBox.isPriority()) {
                    ChargeBox l0 = priorityQueue.removeFirst();
                    anschlussLoad -= l0.getCurrentLoad();
                    tmpSuspendedList.add(l0);
                } else if (!chargingList.isEmpty()) {
                    ChargeBox ln = chargingList.removeFirst();
                    anschlussLoad -= ln.getCurrentLoad();
                    tmpSuspendedList.add(ln);
                }
                if (chargingList.isEmpty() && !chargeBox.isPriority()) {
                    break;
                }
            }
            add(chargeBox);
            // Chargingbox hat verbraucht zu viel
            if (revertStartingIfNeeded(chargeBox)) {
                if (!chargingList.isEmpty() && calculateFitting(anschlussLoad - chargingList.getFirst().getCurrentLoad())) {
                    ChargeBox l0 = chargingList.removeFirst();
                    tmpSuspendedList.add(l0);
                    anschlussLoad = anschluss.getCurrentLoad() - l0.getCurrentLoad();
                }
            }
            decreaseLoad();
        }
    }

    @Override
    public boolean add(ChargeBox chargeBox) {
        if (chargeBox.isPriority()) {
            return priorityQueue.add(chargeBox);
        } else {
            return chargingList.add(chargeBox);
        }
    }

    @Override
    public boolean remove(ChargeBox chargeBox) {
        if (chargeBox.isPriority()) {
            return priorityQueue.remove(chargeBox);
        } else {
            return chargingList.remove(chargeBox);
        }
    }

    @Override
    public void removeLV(ChargeBox chargeBox) {
        if (!remove(chargeBox))
            suspendedList.remove(chargeBox);
        //calculateFitting(anschluss.getCurrentLoad());
    }

    public String printPrioryList() {
        return getString(priorityQueue);
    }

    private void decreaseLoad() throws NotStoppedException {
        while (anschlussLoad > anschluss.getHardLimit()) {
            // Keine non-Prio LV mehr, entferne Prio
            if (chargingList.isEmpty()) {
                ChargeBox l0 = priorityQueue.removeFirst();
                anschlussLoad -= l0.getCurrentLoad();
                tmpSuspendedList.add(l0);
            } else {
                ChargeBox ln = chargingList.removeFirst();
                anschlussLoad -= ln.getCurrentLoad();
                tmpSuspendedList.add(ln);
            }
        }
        calculateFitting(anschlussLoad);
        for (ChargeBox chargeBox : tmpSuspendedList) {
            stop(chargeBox);
        }
        suspendedList.addAll(tmpSuspendedList);
        tmpSuspendedList.clear();
        anschlussLoad = anschluss.getCurrentLoad();
    }
}
