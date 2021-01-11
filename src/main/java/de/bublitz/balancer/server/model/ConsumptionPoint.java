package de.bublitz.balancer.server.model;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import java.time.Instant;

@Data
@Measurement(name = "consumption")
public class ConsumptionPoint {

    public ConsumptionPoint() {
        time = Instant.now();
    }

    public ConsumptionPoint(String name, double consumption) {
        this.time = Instant.now();
        this.consumption = consumption;
        this.name = name;
    }

    public ConsumptionPoint(String name, double consumption, Instant time, String measurand) {
        this.time = time;
        this.consumption = consumption;
        this.name = name;
        this.measurand = measurand;
    }

    @TimeColumn
    @Column(name = "time")
    private Instant time;

    @Column(name = "name", tag = true)
    private String name;

    @Column(name = "consumption")
    private Double consumption;

    @Column(name = "measurand")
    private String measurand;

}
