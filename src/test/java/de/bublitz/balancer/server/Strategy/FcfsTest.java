package de.bublitz.balancer.server.Strategy;

import de.bublitz.balancer.server.components.strategien.FirstComeFirstServeStrategy;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.lang.Thread.sleep;

@SpringBootTest
@Log4j2
public class FcfsTest {
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

    @BeforeClass
    public void SetUp() {
        anschluss = new Anschluss();
        anschluss.setMaxLoad(30);
        anschluss.setHardLimit(30);

        // Chargeboxes
        chargeBox1 = new ChargeBox();
        chargeBox1.setName("CB1");
        chargeBox2 = new ChargeBox();
        chargeBox2.setName("CB2");
        chargeBox3 = new ChargeBox();
        chargeBox3.setName("CB3");
        chargeBox4 = new ChargeBox();
        chargeBox4.setName("CB4");

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
    }

    @Test
    public void KnapsackTest() throws Exception {
        chargeBox1.setCurrentLoad(4);
        fcfs.addLV(chargeBox1);
        log.info("ChargingList: " + fcfs.printChargingList() + " SuspendedList: " + fcfs.printSuspendedList()
                + " Current Load: " + anschluss.getCurrentLoad());
        incCounter();

        chargeBox3.setCurrentLoad(10);
        fcfs.addLV(chargeBox3);
        log.info("ChargingList: " + fcfs.printChargingList() + " SuspendedList: " + fcfs.printSuspendedList()
                + " Current Load: " + anschluss.getCurrentLoad());
        incCounter();

        chargeBox2.setCurrentLoad(7);
        fcfs.addLV(chargeBox2);
        log.info("ChargingList: " + fcfs.printChargingList() + " SuspendedList: " + fcfs.printSuspendedList()
                + " Current Load: " + anschluss.getCurrentLoad());
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(10);
        fcfs.addLV(chargeBox4);
        fcfs.getSuspended().get(0).setCurrentLoad(0);
        anschluss.computeLoad();
        log.info("ChargingList: " + fcfs.printChargingList() + " SuspendedList: " + fcfs.printSuspendedList()
                + " Current Load: " + anschluss.getCurrentLoad());
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
            log.info("ChargingList: " + fcfs.printChargingList() + " SuspendedList: " + fcfs.printSuspendedList()
                    + " Current Load: " + anschluss.getCurrentLoad());
            sleep(1000);
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
