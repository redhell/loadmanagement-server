package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;

/**
 * Beendet Ladevorgang wenn MAX Last vom Anschluss erreicht bzw am limit ist
 */
public class PriorityQueueStrategy extends Strategy {

    public PriorityQueueStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.PQ);
    }

    @Override
    public void optimize() {

    }

    @Override
    public void addLV(ChargeBox chargeBox) {

    }
}
