package de.bublitz.balancer.server.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
@Data
public abstract class AbstractConsumer {

    @Column(unique = true)
    private String name;
    /**
     * currentLoad in Ampere
     */
    private double currentLoad;
    /**
     * Current Voltage in Volt
     */
    private double voltage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_anschluss")
    private Anschluss anschluss;

    public AbstractConsumer() {
        currentLoad = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractConsumer)) return false;

        AbstractConsumer that = (AbstractConsumer) o;

        if (Double.compare(that.voltage, voltage) != 0) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(anschluss, that.anschluss);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(currentLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(voltage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (anschluss != null ? anschluss.hashCode() : 0);
        return result;
    }
}
