package de.bublitz.balancer.server.Strategy;

import de.bublitz.balancer.server.components.strategien.Strategy;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

import static java.lang.Thread.sleep;

@Log4j2
public abstract class GeneralTest extends AbstractTestNGSpringContextTests {
    protected ChargeBox chargeBox1;
    protected ChargeBox chargeBox2;
    protected ChargeBox chargeBox3;
    protected ChargeBox chargeBox4;
    protected ChargeBox chargeBox5;

    protected Anschluss anschluss;
    protected int zeitpunkt = 0;
    protected Strategy strategy;
    private int counterCB1 = 0;
    private int counterCB2 = 0;
    private int counterCB3 = 0;
    private int counterCB4 = 0;
    private int counterCB5 = 0;
    private int suspenedCounterCB1 = 0;
    private int suspenedCounterCB2 = 0;
    private int suspenedCounterCB3 = 0;
    private int suspenedCounterCB4 = 0;
    private int suspenedCounterCB5 = 0;

    protected void prepare() {
        anschluss = new Anschluss();
        anschluss.setMaxLoad(35);
        anschluss.setHardLimit(30);

        // Chargeboxes
        chargeBox1 = new ChargeBox();
        chargeBox1.setName("CB1");
        chargeBox1.setEvseid("CB1");
        chargeBox2 = new ChargeBox();
        chargeBox2.setName("CB2");
        chargeBox2.setEvseid("CB2");
        chargeBox3 = new ChargeBox();
        chargeBox3.setName("CB3");
        chargeBox3.setEvseid("CB3");
        chargeBox4 = new ChargeBox();
        chargeBox4.setName("CB4");
        chargeBox4.setEvseid("CB4");
        chargeBox5 = new ChargeBox();
        chargeBox5.setName("CB5");
        chargeBox5.setEvseid("CB5");

        chargeBox1.setStartURL("testStart");
        chargeBox2.setStartURL("testStart");
        chargeBox3.setStartURL("testStart");
        chargeBox4.setStartURL("testStart");
        chargeBox5.setStartURL("testStart");

        chargeBox1.setStopURL("testStop");
        chargeBox2.setStopURL("testStop");
        chargeBox3.setStopURL("testStop");
        chargeBox4.setStopURL("testStop");
        chargeBox5.setStopURL("testStop");

        chargeBox1.setConnected(true);
        chargeBox2.setConnected(true);
        chargeBox3.setConnected(true);
        chargeBox4.setConnected(true);
        chargeBox5.setConnected(true);

        // Zum Anschluss
        anschluss.addChargeBox(chargeBox1);
        anschluss.addChargeBox(chargeBox2);
        anschluss.addChargeBox(chargeBox3);
        anschluss.addChargeBox(chargeBox4);
        anschluss.addChargeBox(chargeBox5);


        counterCB1 = 0;
        counterCB2 = 0;
        counterCB3 = 0;
        counterCB4 = 0;
        counterCB5 = 0;
        zeitpunkt = 0;

        suspenedCounterCB1 = 0;
        suspenedCounterCB2 = 0;
        suspenedCounterCB3 = 0;
        suspenedCounterCB4 = 0;
        suspenedCounterCB5 = 0;
    }

    protected void incCounter() {
        zeitpunkt++;
        strategy.getChargingList().forEach(this::checkAndIncrease);
        strategy.getSuspendedList().forEach(this::checkAndIncreaseSuspended);
    }

    protected void checkAndIncrease(ChargeBox chargeBox) {
        if (chargeBox.equals(chargeBox1)) {
            counterCB1++;
        } else if (chargeBox.equals(chargeBox2)) {
            counterCB2++;
        } else if (chargeBox.equals(chargeBox3)) {
            counterCB3++;
        } else if (chargeBox.equals(chargeBox4)) {
            counterCB4++;
        } else if (chargeBox.equals(chargeBox5)) {
            counterCB5++;
        }
    }

    private void checkAndIncreaseSuspended(ChargeBox chargeBox) {
        if (chargeBox.equals(chargeBox1)) {
            suspenedCounterCB1++;
        } else if (chargeBox.equals(chargeBox2)) {
            suspenedCounterCB2++;
        } else if (chargeBox.equals(chargeBox3)) {
            suspenedCounterCB3++;
        } else if (chargeBox.equals(chargeBox4)) {
            suspenedCounterCB4++;
        } else if (chargeBox.equals(chargeBox5)) {
            suspenedCounterCB5++;
        }
    }


    protected void log() {
        log.info("ChargingList: " + strategy.printChargingList()
                + " SuspendedList: " + strategy.printSuspendedList()
                + " Consumers: " + strategy.printConsumerLoad()
                + " Load: " + anschluss.getCurrentLoad()
                + " T: " + zeitpunkt);
    }

    protected void checkIfFinished() throws NotStoppedException {
        if (counterCB1 == 8) {
            chargeBox1.setCurrentLoad(0);
            strategy.removeLV(chargeBox1);
            log.info("CB1 Ende: " + zeitpunkt + " Unterbrechungen: " + suspenedCounterCB1);
            counterCB1++;
        }
        if (counterCB2 == 5) {
            chargeBox2.setCurrentLoad(0);
            strategy.removeLV(chargeBox2);
            log.info("CB2 Ende: " + zeitpunkt + " Unterbrechungen: " + suspenedCounterCB2);
            counterCB2++;
        }
        if (counterCB3 == 6) {
            chargeBox3.setCurrentLoad(0);
            strategy.removeLV(chargeBox3);
            log.info("CB3 Ende: " + zeitpunkt + " Unterbrechungen: " + suspenedCounterCB3);
            counterCB3++;
        }
        if (counterCB4 == 4) {
            chargeBox4.setCurrentLoad(0);
            strategy.removeLV(chargeBox4);
            log.info("CB4 Ende: " + zeitpunkt + " Unterbrechungen: " + suspenedCounterCB4);
            counterCB4++;
        }
        if (counterCB5 == 2) {
            chargeBox5.setCurrentLoad(0);
            strategy.removeLV(chargeBox5);
            log.info("CB5 Ende: " + zeitpunkt + " Unterbrechungen: " + suspenedCounterCB5);
            counterCB5++;
        }
    }

    protected void charge() throws NotStoppedException, InterruptedException {
        checkIfFinished();

        strategy.optimize();

        anschluss.computeLoad();
        log();
        incCounter();
        Assert.assertTrue(anschluss.getCurrentLoad() <= anschluss.getHardLimit());
        sleep(100);
    }
}
