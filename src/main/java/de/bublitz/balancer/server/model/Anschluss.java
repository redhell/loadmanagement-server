package de.bublitz.balancer.server.model;

import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Anschluss {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Consumer> consumerList;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    private List<ChargeBox> chargeBoxList;
    private float maxLoad;
    private LoadStrategy loadStrategy;
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    public Anschluss() {
        consumerList = new ArrayList<>();
        chargeBoxList = new ArrayList<>();
        maxLoad = 0;
        loadStrategy = LoadStrategy.NONE;
        name = "Anschluss";
    }

    public void addConsumer(Consumer consumer) {
        consumerList.add(consumer);
    }

    public void addChargeBox(ChargeBox chargeBox) {
        chargeBoxList.add(chargeBox);
    }
}
