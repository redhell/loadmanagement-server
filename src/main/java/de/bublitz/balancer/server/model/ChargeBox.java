package de.bublitz.balancer.server.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

/**
 * Eine Chargebox ist ein besonderer Verbraucher, welcher angesteuert werden kann
 */


@Data
@Entity
@Table(name = "chargeboxes")
public class ChargeBox extends AbstractConsumer {

    /**
     * priority = true/false
     * chargeboxid = Ladepunktnummer
     * startURL = URL zum starten des LV
     * stopURL = URL zum stoppen
     * emaid = vertragsnummer/tag zum starten bspw.
     */
    private boolean priority;
    @Column(unique = true)
    private String evseid;
    private String startURL;
    private String stopURL;
    private double idleConsumption;
    private String emaid;

    private boolean charging;
    private boolean connected;
    private boolean calibrated;
    private double lastLoad;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long chargeboxId;


    public ChargeBox() {
        super();

        emaid = "";
        lastLoad = 0;
        evseid = "";
        idleConsumption = 0;
        charging = false;
        connected = true;
        calibrated = false;
        priority = false;
        setName("ChargeBox");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChargeBox)) return false;
        if (!super.equals(o)) return false;

        ChargeBox chargeBox = (ChargeBox) o;

        if (priority != chargeBox.priority) return false;
        if (connected != chargeBox.connected) return false;
        if (calibrated != chargeBox.calibrated) return false;
        if (!Objects.equals(evseid, chargeBox.evseid)) return false;
        if (!Objects.equals(startURL, chargeBox.startURL)) return false;
        if (!Objects.equals(stopURL, chargeBox.stopURL)) return false;
        if (!Objects.equals(emaid, chargeBox.emaid)) return false;
        return Objects.equals(chargeboxId, chargeBox.chargeboxId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (priority ? 1 : 0);
        result = 31 * result + (evseid != null ? evseid.hashCode() : 0);
        result = 31 * result + (startURL != null ? startURL.hashCode() : 0);
        result = 31 * result + (stopURL != null ? stopURL.hashCode() : 0);
        result = 31 * result + (emaid != null ? emaid.hashCode() : 0);
        result = 31 * result + (connected ? 1 : 0);
        result = 31 * result + (calibrated ? 1 : 0);
        result = 31 * result + (chargeboxId != null ? chargeboxId.hashCode() : 0);
        return result;
    }

}
