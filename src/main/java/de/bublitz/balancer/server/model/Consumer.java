package de.bublitz.balancer.server.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Consumer ist ein statischer Verbraucher, welcher nicht gesteuert werden kann.
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Consumer {

    private float maxLoad;
    private float currentLoad;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public Consumer() {
        maxLoad = 0;
        currentLoad = 0;
        name = "Consumer";
    }

}
