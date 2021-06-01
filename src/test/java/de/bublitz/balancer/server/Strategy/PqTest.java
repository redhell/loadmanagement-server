package de.bublitz.balancer.server.Strategy;

import de.bublitz.balancer.server.components.strategien.PriorityQueueStrategy;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.model.exception.NotStoppedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.lang.Thread.sleep;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
@Log4j2
public class PqTest extends AbstractTestNGSpringContextTests {

    private ChargeBox chargeBox1;
    private ChargeBox chargeBox2;
    private ChargeBox chargeBox3;
    private ChargeBox chargeBox4;
    private ChargeBox chargeBox5;

    private Anschluss anschluss;

    private int counterCB1 = 0;
    private int counterCB2 = 0;
    private int counterCB3 = 0;
    private int counterCB4 = 0;
    private int counterCB5 = 0;
    private int zeitpunkt = 0;

    private PriorityQueueStrategy pq;

    @BeforeMethod
    public void SetUp() {

        anschluss = new Anschluss();
        anschluss.setMaxLoad(35);
        anschluss.setHardLimit(30);
        anschluss.setLoadStrategy(LoadStrategy.PQ);

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

        pq = new PriorityQueueStrategy(anschluss);

        counterCB1 = 0;
        counterCB2 = 0;
        counterCB3 = 0;
        counterCB4 = 0;
        counterCB5 = 0;
        zeitpunkt = 0;

        chargeBox2.setPriority(true);
    }

    @Test
    public void onlyChargeboxesTest() throws Exception {
        log.info("Starting Basic Test!");
        chargeBox1.setCurrentLoad(4);
        chargeBox1.setCharging(true);
        pq.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        pq.addLV(chargeBox3);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(7);
        chargeBox2.setCharging(true);
        pq.addLV(chargeBox2);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        pq.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        // 2. Balancing
        chargeBox5.setCurrentLoad(22);
        chargeBox5.setCharging(true);
        pq.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!pq.getPriorityQueue().isEmpty() || !pq.getChargingList().isEmpty() || !pq.getSuspendedList().isEmpty()) {
            pq.optimize();

            checkIfFinished();

            anschluss.computeLoad();
            incCounter();
            log();
            Assert.assertTrue(anschluss.getCurrentLoad() <= anschluss.getHardLimit());
            sleep(100);
        }
        Assert.assertTrue(pq.getChargingList().isEmpty() && pq.getSuspendedList().isEmpty());
    }

    @Test
    public void chargeboxesWithConsumersTest() throws Exception {
        log.info("chargeboxesWithConsumers Test");
        Consumer consumer = new Consumer();
        consumer.setCurrentLoad(1.5);
        anschluss.addConsumer(consumer);
        chargeBox1.setCurrentLoad(4);
        chargeBox1.setCharging(true);
        pq.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        pq.addLV(chargeBox3);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(7);
        chargeBox2.setCharging(true);
        pq.addLV(chargeBox2);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        pq.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        // 2. Balancing
        chargeBox5.setCurrentLoad(22);
        chargeBox5.setCharging(true);
        pq.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        log.info("Erhöhe Consumer");
        consumer.setCurrentLoad(8);
        //anschluss.computeLoad();

        while (!pq.getChargingList().isEmpty() || !pq.getSuspendedList().isEmpty()) {
            pq.optimize();

            checkIfFinished();

            anschluss.computeLoad();
            incCounter();
            log();
            sleep(100);
            Assert.assertTrue(anschluss.getCurrentLoad() <= anschluss.getHardLimit());
        }
        Assert.assertTrue(pq.getChargingList().isEmpty() && pq.getSuspendedList().isEmpty());
    }

    private void incCounter() {
        zeitpunkt++;
        pq.getChargingList().forEach(this::checkAndIncrease);
        pq.getPriorityQueue().forEach(this::checkAndIncrease);
    }

    private void checkAndIncrease(ChargeBox chargeBox) {
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

    private void log() {
        log.info("Priority: " + pq.printPrioryList()
                + " ChargingList: " + pq.printChargingList()
                + " SuspendedList: " + pq.printSuspendedList()
                + " Consumers: " + pq.printConsumerLoad()
                + " Load: " + anschluss.getCurrentLoad()
                + " T: " + zeitpunkt);
    }

    private void checkIfFinished() throws NotStoppedException {
        if (counterCB1 == 8) {
            pq.removeLV(chargeBox1);
            chargeBox1.setCurrentLoad(0);
            log.info("CB1 Ende: " + zeitpunkt);
            counterCB1++;
        }
        if (counterCB2 == 5) {
            pq.removeLV(chargeBox2);
            chargeBox2.setCurrentLoad(0);
            log.info("CB2 Ende: " + zeitpunkt);
            counterCB2++;
        }
        if (counterCB3 == 6) {
            pq.removeLV(chargeBox3);
            chargeBox3.setCurrentLoad(0);
            log.info("CB3 Ende: " + zeitpunkt);
            counterCB3++;
        }
        if (counterCB4 == 4) {
            pq.removeLV(chargeBox4);
            chargeBox4.setCurrentLoad(0);
            log.info("CB4 Ende: " + zeitpunkt);
            counterCB4++;
        }
        if (counterCB5 == 2) {
            pq.removeLV(chargeBox5);
            chargeBox5.setCurrentLoad(0);
            log.info("CB5 Ende: " + zeitpunkt);
            counterCB5++;
        }
    }
}