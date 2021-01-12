package de.bublitz.balancer.server.model.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.enums.LoadStrategy;

/**
 * Beendet Ladevorgang wenn MAX Last vom Anschluss erreicht bzw am limit ist
 */
public class SimpleStrategy extends Strategy {

    public SimpleStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.SIMPLE);
    }
}
