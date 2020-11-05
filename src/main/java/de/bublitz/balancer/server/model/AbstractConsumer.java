package de.bublitz.balancer.server.model;

import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public abstract class AbstractConsumer {

    private String name;
    private double currentLoad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_anschluss")
    private Anschluss anschluss;

    public AbstractConsumer() {
        currentLoad = 0;
    }
}
