package de.bublitz.balancer.server.model;

import de.bublitz.balancer.server.controller.strategies.Strategy;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anschluss")
@Data
public class Anschluss {

    @OneToMany(mappedBy = "anschluss")
    private List<Consumer> consumerList;
    @OneToMany(mappedBy = "anschluss")
    private List<ChargeBox> chargeboxList;
    private double maxLoad;

    private LoadStrategy loadStrategy;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @OneToOne(mappedBy = "anschluss")
    private Strategy strategy;

    public Anschluss() {
        consumerList = new ArrayList<>();
        chargeboxList = new ArrayList<>();
        maxLoad = 0;
        loadStrategy = LoadStrategy.NONE;
        name = "Anschluss";
    }

    public void addConsumer(Consumer consumer) {
        consumer.setAnschluss(this);
        consumerList.add(consumer);
    }

    public void addChargeBox(ChargeBox chargeBox) {
        chargeBox.setAnschluss(this);
        chargeboxList.add(chargeBox);
    }
}
