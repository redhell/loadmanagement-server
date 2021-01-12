package de.bublitz.balancer.server.model.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LastInFirstOutStrategy extends Strategy {

    public LastInFirstOutStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.LIFO);
    }
}
