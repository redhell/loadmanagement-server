package de.bublitz.balancer.server.model.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.enums.LoadStrategy;

public class FirstInFirstOutStrategy extends Strategy {
    public FirstInFirstOutStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.FIFO);
    }
}
