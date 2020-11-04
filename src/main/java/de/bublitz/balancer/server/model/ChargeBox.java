package de.bublitz.balancer.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Eine Chargebox ist ein besonderer Verbraucher, welcher angesteuert werden kann
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ChargeBox extends Consumer {

    /**
     * priority = 100 (ganz wichtig) 0 (unwichtig)
     * chargeboxid = Ladepunktnummer
     * startURL = URL zum starten des LV
     * stopURL = URL zum stoppen
     * outlet = Ladepunkt (physisch)
     * emaid = vertragsnummer/tag zum starten bspw.
     */
    private int priority = 100;
    private String chargeboxID;
    private URL startURL;
    private URL stopURL;

    private int outlet;
    private String emaid;

    public ChargeBox() {
        super();
        emaid = "";
        chargeboxID = "";
        setName("ChargeBox");
        outlet = 1;
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
