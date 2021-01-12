package de.bublitz.balancer.server.model.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.Data;

@Data
public abstract class Strategy {

    private final LoadStrategy loadStrategy;

    private Anschluss anschluss;

    public Strategy(Anschluss anschluss, LoadStrategy loadStrategy) {
        this.loadStrategy = loadStrategy;
    }
}
