package de.bublitz.balancer.server.model;

import lombok.Data;

import javax.persistence.*;

@MappedSuperclass
@Data
public abstract class AbstractConsumer {

    private String name;
    /**
     * currentLoad in Ampere
     */
    private double currentLoad;
    /**
     * Current Voltage in Volt
     */
    private double voltage;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_anschluss")
    private Anschluss anschluss;

    public AbstractConsumer() {
        currentLoad = 0;
    }
}
