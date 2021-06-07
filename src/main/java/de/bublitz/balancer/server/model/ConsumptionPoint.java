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

    @Column(name = "cbName", tag = true)
    private String cbName;
    @Column(name = "name")
    private String name;

    @TimeColumn
    @Column(name = "time")
    private Instant time;

    public ConsumptionPoint(String cbName, double consumption) {
        this.time = Instant.now();
        this.consumption = consumption;
        this.cbName = cbName;
        this.name = cbName;
    }

    public ConsumptionPoint(String cbName, double consumption, Instant time, String measurand) {
        this.time = time;
        this.consumption = consumption;
        this.cbName = cbName;
        this.measurand = measurand;
        this.name = cbName;
    }

    @Column(name = "consumption")
    private Double consumption;

    @Column(name = "measurand")
    private String measurand;

}
