package de.bublitz.balancer.server.controller.strategies;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.Data;

import javax.persistence.*;
import java.util.concurrent.atomic.AtomicReference;

@Entity
@Data
public class Strategy {
    /**
     * MaxLoad = maximale Last die möglich ist
     */
    private float maxLoad = 0;
    /**
     * softLimit = Ab dem soft Limit kann agiert werden, aber muss nicht zwingend
     */
    private float softLimit = 0;
    /**
     * hardLimit = Ab dem harten limit muss agiert werden! Kann maxLoad entsprechen
     */
    private float hardLimit = 0;
    private LoadStrategy loadStrategy;

    @OneToOne
    @JoinColumn
    private Anschluss anschluss;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Strategy() {
        loadStrategy = LoadStrategy.NONE;
    }

    public boolean isSoftlimitReached() {
        return computeLoad(softLimit);
    }

    public boolean isHardlimitReached() {
        return computeLoad(hardLimit);
    }

    private boolean computeLoad(double limit) {
        AtomicReference<Double> load = new AtomicReference<>((double) 0);
        anschluss.getConsumerList().forEach(consumer -> load.updateAndGet(v -> v + consumer.getCurrentLoad()));
        anschluss.getChargeboxList().forEach(chargeBox -> load.updateAndGet(v -> v + chargeBox.getCurrentLoad()));
        return !(load.get() <= limit);
    }
}
