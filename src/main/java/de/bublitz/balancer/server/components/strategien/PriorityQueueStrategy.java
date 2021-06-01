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
            stopAndRemove();
            anschluss.computeLoad();
            //calculateFitting(anschlussLoad);
        }
    }

    private void stopAndRemove() throws NotStoppedException {
        while (anschlussLoad > anschluss.getHardLimit()) {
            // Keine non-Prio LV mehr, entferne Prio
            if (chargingList.isEmpty()) {
                ChargeBox l0 = priorityQueue.removeFirst();
                anschlussLoad -= l0.getCurrentLoad();
                tmpSuspendedList.add(l0);
            } else {
                ChargeBox ln = chargingList.removeLast();
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
        anschluss.computeLoad();
    }

    @Override
    public void addLV(ChargeBox chargeBox) throws NotStoppedException {
        anschlussLoad = anschluss.getCurrentLoad();
        if (anschlussLoad <= anschluss.getHardLimit()) {
            add(chargeBox);
        } else {
            while (anschlussLoad > anschluss.getHardLimit()) {
                // Keine non-Prio LV mehr, entferne Prio
                if (chargingList.isEmpty() && !priorityQueue.isEmpty() && chargeBox.isPriority()) {
                    ChargeBox l0 = priorityQueue.removeFirst();
                    anschlussLoad -= l0.getCurrentLoad();
                    tmpSuspendedList.add(l0);
                } else if (!chargingList.isEmpty()) {
                    ChargeBox ln = chargingList.removeLast();
                    anschlussLoad -= ln.getCurrentLoad();
                    tmpSuspendedList.add(ln);
                }
                if (chargingList.isEmpty() && !chargeBox.isPriority()) {
                    break;
                }
            }
            calculateFitting(anschlussLoad);
            add(chargeBox);
            // Chargingbox hat verbraucht zu viel
            if (anschlussLoad > anschluss.getHardLimit()) {
                if (chargeBox.isCharging()) {
                    stop(chargeBox);
                } else {
                    chargeBox.setLastLoad(chargeBox.getCurrentLoad());
                    chargeBox.setCurrentLoad(0);
                }
                suspendedList.add(chargeBox);
                remove(chargeBox);
                tmpSuspendedList.forEach(this::add);
                tmpSuspendedList.clear();
            }
        }
        for (ChargeBox box : tmpSuspendedList) {
            stop(box);
        }
        suspendedList.addAll(tmpSuspendedList);
        tmpSuspendedList.clear();
        anschluss.computeLoad();
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
    }

    public String printPrioryList() {
        return getString(priorityQueue);
    }
}
