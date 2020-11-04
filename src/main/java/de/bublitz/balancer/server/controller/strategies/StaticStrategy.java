package de.bublitz.balancer.server.controller.strategies;

import de.bublitz.balancer.server.model.enums.LoadStrategy;

/**
 * Beendet Ladevorgang wenn MAX Last vom Anschluss erreicht bzw am limit ist
 */
public class StaticStrategy extends Strategy {

    public StaticStrategy() {
        super();
        setLoadStrategy(LoadStrategy.STATIC);
    }



    public void stopCharging() {

    }

    public void startCharging() {

    }
}
