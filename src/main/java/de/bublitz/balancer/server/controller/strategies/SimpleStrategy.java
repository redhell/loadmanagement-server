package de.bublitz.balancer.server.controller.strategies;

import de.bublitz.balancer.server.model.enums.LoadStrategy;

import javax.persistence.Entity;

/**
 * Beendet Ladevorgang wenn MAX Last vom Anschluss erreicht bzw am limit ist
 */
@Entity
public class SimpleStrategy extends Strategy {

    public SimpleStrategy() {
        super();
        setLoadStrategy(LoadStrategy.SIMPLE);
    }


    public void stopCharging() {
        if (isHardlimitReached()) {
            // StopCharging
        }
    }

    public void startCharging() {

    }
}
