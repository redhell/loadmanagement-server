package de.bublitz.balancer.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Eine Chargebox ist ein besonderer Verbraucher, welcher angesteuert werden kann
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "chargeboxes")
public class ChargeBox extends AbstractConsumer {

    /**
     * priority = 100 (ganz wichtig) 0 (unwichtig)
     * chargeboxid = Ladepunktnummer
     * startURL = URL zum starten des LV
     * stopURL = URL zum stoppen
     * emaid = vertragsnummer/tag zum starten bspw.
     */
    private int priority = 100;
    @Column(unique = true)
    private String evseid;
    private URL startURL;
    private URL stopURL;
    private double idleConsumption;
    private String emaid;
    private boolean charging;
    private boolean isConnected;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long chargeboxId;


    public ChargeBox() {
        super();
        emaid = "";
        evseid = "";
        idleConsumption = 0;
        charging = false;
        isConnected = true;
        setName("ChargeBox");
    }

    public void setStartURL(String url) {
        try {
            startURL = new URL(url);
        } catch (MalformedURLException e) {
            startURL = null;
        }
    }

    public void setStopURL(String url)  {
        try {
            stopURL = new URL(url);
        } catch (MalformedURLException e) {
            stopURL = null;
        }
    }
}
