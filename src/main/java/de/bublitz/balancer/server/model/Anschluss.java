package de.bublitz.balancer.server.model;

import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Entity
@Table(name = "anschluss")
@Data
public class Anschluss {

    /**
     * MaxLoad = maximale Last die möglich ist
     */
    private double maxLoad = 0;
    /**
     * softLimit = Ab dem soft Limit kann agiert werden, aber muss nicht zwingend
     */
    private double softLimit = 0;
    /**
     * hardLimit = Ab dem harten limit muss agiert werden! Kann maxLoad entsprechen
     */
    private double hardLimit = 0;

    @OneToMany(mappedBy = "anschluss", cascade = CascadeType.ALL)
    private List<Consumer> consumerList;
    @OneToMany(mappedBy = "anschluss", cascade = CascadeType.ALL)
    private List<ChargeBox> chargeboxList;
    private double currentLoad;

    private LoadStrategy loadStrategy;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public Anschluss() {
        consumerList = new ArrayList<>();
        chargeboxList = new ArrayList<>();
        maxLoad = 0;
        currentLoad = 0;
        loadStrategy = LoadStrategy.SIMPLE;
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

    public boolean isSoftlimitReached() {
        return currentLoad >= softLimit;
    }

    public boolean isHardlimitReached() {
        return currentLoad >= hardLimit;
    }

    public void computeLoad() {
        AtomicReference<Double> load = new AtomicReference<>((double) 0);
        consumerList.forEach(consumer -> load.updateAndGet(v -> v + consumer.getCurrentLoad()));
        // Only compute connected load!
        chargeboxList.stream().filter(ChargeBox::isConnected)
                .forEach(chargeBox -> load.updateAndGet(v -> v + chargeBox.getCurrentLoad()));
        currentLoad = load.get();
    }

    public void sendStopCharging(ChargeBox chargeBox) {
    }

    public void sendStartCharging(ChargeBox chargeBox) {

    }
}
