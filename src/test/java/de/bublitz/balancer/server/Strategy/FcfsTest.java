package de.bublitz.balancer.server.Strategy;

import de.bublitz.balancer.server.components.strategien.FirstComeFirstServeStrategy;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
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
public class FcfsTest extends AbstractTestNGSpringContextTests {
    ChargeBox chargeBox1;
    ChargeBox chargeBox2;
    ChargeBox chargeBox3;
    ChargeBox chargeBox4;
    private Anschluss anschluss;
    private int counterCB1 = 0;
    private int counterCB2 = 0;
    private int counterCB3 = 0;
    private int counterCB4 = 0;

    private FirstComeFirstServeStrategy fcfs;

    @BeforeMethod
    public void SetUp() {
        anschluss = new Anschluss();
        anschluss.setMaxLoad(30);
        anschluss.setHardLimit(30);
        anschluss.setLoadStrategy(LoadStrategy.FCFS);

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

        chargeBox1.setStartURL("testStart");
        chargeBox2.setStartURL("testStart");
        chargeBox3.setStartURL("testStart");
        chargeBox4.setStartURL("testStart");

        chargeBox1.setStopURL("testStop");
        chargeBox2.setStopURL("testStop");
        chargeBox3.setStopURL("testStop");
        chargeBox4.setStopURL("testStop");

        chargeBox1.setConnected(true);
        chargeBox2.setConnected(true);
        chargeBox3.setConnected(true);
        chargeBox4.setConnected(true);

        // Zum Anschluss
        anschluss.addChargeBox(chargeBox1);
        anschluss.addChargeBox(chargeBox2);
        anschluss.addChargeBox(chargeBox3);
        anschluss.addChargeBox(chargeBox4);

        fcfs = new FirstComeFirstServeStrategy(anschluss);

        counterCB1 = 0;
        counterCB2 = 0;
        counterCB3 = 0;
        counterCB4 = 0;
    }

    @Test
    public void onlyChargeboxesTest() throws Exception {
        log.info("Starting Basic Test!");
        chargeBox1.setCurrentLoad(4);
        fcfs.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(10);
        fcfs.addLV(chargeBox3);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(7);
        fcfs.addLV(chargeBox2);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(10);
        fcfs.addLV(chargeBox4);
        fcfs.getSuspended().get(0).setCurrentLoad(0);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!fcfs.getChargingList().isEmpty()) {
            fcfs.optimize();

            fcfs.getSuspended().forEach(chargeBox -> {
                chargeBox.setCurrentLoad(0);
            });

            fcfs.getChargingList().forEach(chargeBox -> {
                if (chargeBox.equals(chargeBox1)) {
                    chargeBox1.setCurrentLoad(4);
                } else if (chargeBox.equals(chargeBox2)) {
                    chargeBox2.setCurrentLoad(7);
                } else if (chargeBox.equals(chargeBox3)) {
                    chargeBox3.setCurrentLoad(10);
                } else if (chargeBox.equals(chargeBox4)) {
                    chargeBox4.setCurrentLoad(10);
                }
            });
            anschluss.computeLoad();

            if (counterCB1 == 8) {
                fcfs.removeLV(chargeBox1);
                chargeBox1.setCurrentLoad(0);
            }
            if (counterCB2 == 5) {
                fcfs.removeLV(chargeBox2);
                chargeBox2.setCurrentLoad(0);
            }
            if (counterCB3 == 6) {
                fcfs.removeLV(chargeBox3);
                chargeBox3.setCurrentLoad(0);
            }
            if (counterCB4 == 4) {
                fcfs.removeLV(chargeBox4);
                chargeBox4.setCurrentLoad(0);
            }
            anschluss.computeLoad();
            incCounter();
            log();
            Assert.assertTrue(anschluss.getCurrentLoad() < anschluss.getHardLimit());
            sleep(200);
        }

    }

    private void log() {
        log.info("ChargingList: " + fcfs.printChargingList()
                + " SuspendedList: " + fcfs.printSuspendedList()
                + " Consumers: " + fcfs.printConsumerLoad()
                + " Current Load: " + anschluss.getCurrentLoad());
    }

    @Test
    public void chargeboxesWithConsumersTest() throws Exception {
        log.info("chargeboxesWithConsumers Test");
        Consumer consumer = new Consumer();
        consumer.setCurrentLoad(1.5);
        anschluss.addConsumer(consumer);
        chargeBox1.setCurrentLoad(4);
        fcfs.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(10);
        fcfs.addLV(chargeBox3);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(7);
        fcfs.addLV(chargeBox2);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(10);
        fcfs.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        consumer.setCurrentLoad(9.5);
        //anschluss.computeLoad();

        while (!fcfs.getChargingList().isEmpty()) {
            fcfs.optimize();

            if (counterCB1 == 8) {
                fcfs.removeLV(chargeBox1);
                chargeBox1.setCurrentLoad(0);
            }
            if (counterCB2 == 5) {
                fcfs.removeLV(chargeBox2);
                chargeBox2.setCurrentLoad(0);
            }
            if (counterCB3 == 6) {
                fcfs.removeLV(chargeBox3);
                chargeBox3.setCurrentLoad(0);
            }
            if (counterCB4 == 4) {
                fcfs.removeLV(chargeBox4);
                chargeBox4.setCurrentLoad(0);
            }
            Assert.assertTrue(anschluss.getCurrentLoad() < anschluss.getHardLimit());
            incCounter();
            log();
            sleep(200);
        }
    }

    private void incCounter() {
        fcfs.getChargingList().forEach(chargeBox -> {
            if (chargeBox.equals(chargeBox1)) {
                counterCB1++;
            } else if (chargeBox.equals(chargeBox2)) {
                counterCB2++;
            } else if (chargeBox.equals(chargeBox3)) {
                counterCB3++;
            } else if (chargeBox.equals(chargeBox4)) {
                counterCB4++;
            }
        });
    }
}
