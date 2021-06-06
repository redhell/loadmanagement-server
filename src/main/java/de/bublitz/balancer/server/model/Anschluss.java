package de.bublitz.balancer.server.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(name = "anschluss")
@Data
public class Anschluss {

    /**
     * MaxLoad = maximale Last die möglich ist
     */
    private double maxLoad;
    /**
     * softLimit = Ab dem soft Limit kann agiert werden, aber muss nicht zwingend
     */
    private double softLimit = 0;
    /**
     * hardLimit = Ab dem harten limit muss agiert werden! Kann maxLoad entsprechen
     */
    private double hardLimit = 0;

    @OneToMany(mappedBy = "anschluss", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Consumer> consumerList;
    @JsonManagedReference
    @OneToMany(mappedBy = "anschluss", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargeBox> chargeboxList;
    private double currentLoad;

    private LoadStrategy loadStrategy;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    public Anschluss() {
        consumerList = new ArrayList<>();
        chargeboxList = new ArrayList<>();
        maxLoad = 0;
        currentLoad = 0;
        loadStrategy = LoadStrategy.FIFO;
        name = "Anschluss" + new Random().nextInt(10000);
    }

    public void addConsumer(Consumer consumer) {
        consumer.setAnschluss(this);
        consumerList.add(consumer);
        computeLoad();
    }

    public void removeConsumer(Consumer consumer) {
        consumerList.remove(consumer);
        computeLoad();
    }

    public void removeChargebox(ChargeBox chargeBox) {
        chargeboxList.remove(chargeBox);
        computeLoad();
    }

    public void addChargeBox(ChargeBox chargeBox) {
        chargeBox.setAnschluss(this);
        chargeboxList.add(chargeBox);
        computeLoad();
    }

    public void computeLoad() {
        double load = 0;
        for (Consumer consumer : consumerList) {
            load += consumer.getCurrentLoad();
        }
        // Only compute connected load!
        for (ChargeBox chargeBox : chargeboxList) {
            if (chargeBox.isConnected()) {
                load += chargeBox.getCurrentLoad();
            }
        }
        currentLoad = load;
    }

    public double getCurrentLoad() {
        computeLoad();
        return currentLoad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Anschluss)) return false;

        Anschluss anschluss = (Anschluss) o;

        if (Double.compare(anschluss.maxLoad, maxLoad) != 0) return false;
        if (Double.compare(anschluss.softLimit, softLimit) != 0) return false;
        if (Double.compare(anschluss.hardLimit, hardLimit) != 0) return false;
        if (loadStrategy != anschluss.loadStrategy) return false;
        return Objects.equals(name, anschluss.name);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(maxLoad);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(softLimit);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(hardLimit);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (loadStrategy != null ? loadStrategy.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
