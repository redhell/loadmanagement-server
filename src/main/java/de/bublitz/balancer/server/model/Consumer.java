package de.bublitz.balancer.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Consumer ist ein statischer Verbraucher, welcher nicht gesteuert werden kann.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "consumers")
public class Consumer extends AbstractConsumer {

    private double maxLoad;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long consumerID;

    public Consumer() {
        super();
        maxLoad = 0;
        setName("Consumer");
    }

}
