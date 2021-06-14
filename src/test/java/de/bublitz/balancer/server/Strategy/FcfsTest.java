package de.bublitz.balancer.server.Strategy;

import de.bublitz.balancer.server.components.strategien.FirstComeFirstServeStrategy;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
@Log4j2
public class FcfsTest extends GeneralTest {

    @BeforeMethod
    public void SetUp() {
        prepare();
        anschluss.setLoadStrategy(LoadStrategy.FCFS);
        strategy = new FirstComeFirstServeStrategy(anschluss);
        //((FirstComeFirstServeStrategy) strategy).setPenalty(4);
    }

    @Test(dataProvider = "zeitscheiben")
    public void a_chargeboxesTest(int zeitscheibe) throws Exception {
        log.info("Starte Test mit Zeitscheibe: " + zeitscheibe);
        chargeBox1.setCurrentLoad(4);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(11);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(7);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        strategy.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        // 2. Balancing
        chargeBox5.setCurrentLoad(22);
        chargeBox5.setCharging(true);
        strategy.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge(zeitscheibe);
        }
        printSchedule();
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }

    @Test(dataProvider = "zeitscheiben")
    public void b_chargeboxesWithConsumersTest(int zeitscheibe) throws Exception {
        log.info("Starte Test mit Zeitscheibe: " + zeitscheibe);
        Consumer consumer = new Consumer();
        consumer.setCurrentLoad(2);
        anschluss.addConsumer(consumer);
        chargeBox1.setCurrentLoad(4);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(11);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(7);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        strategy.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        consumer.setCurrentLoad(9);

        // 2. Balancing
        chargeBox5.setCurrentLoad(22);
        chargeBox5.setCharging(true);
        strategy.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge(zeitscheibe);
            if (zeitpunkt == 10) {
                consumer.setCurrentLoad(4);
            }
        }
        printSchedule();
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }

    @Test(dataProvider = "zeitscheiben")
    public void c_chargeboxesEqualTest(int zeitscheibe) throws Exception {
        log.info("Starte Test mit Zeitscheibe: " + zeitscheibe);
        chargeBox1.setCurrentLoad(11);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(11);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        strategy.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        // 2. Balancing
        chargeBox5.setCurrentLoad(11);
        chargeBox5.setCharging(true);
        strategy.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge(zeitscheibe);
        }
        printSchedule();
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }

    @Test(dataProvider = "zeitscheiben")
    public void d_chargeboxesEqualWithConsumersTest(int zeitscheibe) throws Exception {
        log.info("Starte Test mit Zeitscheibe: " + zeitscheibe);
        Consumer consumer = new Consumer();
        consumer.setCurrentLoad(2);
        anschluss.addConsumer(consumer);
        chargeBox1.setCurrentLoad(11);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(11);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        strategy.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        consumer.setCurrentLoad(9);

        // 2. Balancing
        chargeBox5.setCurrentLoad(11);
        chargeBox5.setCharging(true);
        strategy.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge(zeitscheibe);
            if (zeitpunkt == 10) {
                consumer.setCurrentLoad(4);
            }
        }
        printSchedule();
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }

    @Test(dataProvider = "zeitscheiben")
    public void e_chargeboxesAllEqualTest(int zeitscheibe) throws Exception {
        taskCounterCB1 = 5;
        taskCounterCB2 = 5;
        taskCounterCB3 = 5;
        taskCounterCB4 = 5;
        taskCounterCB5 = 5;

        log.info("Starte Test mit Zeitscheibe: " + zeitscheibe);
        chargeBox1.setCurrentLoad(11);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(11);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        strategy.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        // 2. Balancing
        chargeBox5.setCurrentLoad(11);
        chargeBox5.setCharging(true);
        strategy.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge(zeitscheibe);
        }
        printSchedule();
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }

    @Test(dataProvider = "zeitscheiben")
    public void f_chargeboxesAllEqualConsumersTest(int zeitscheibe) throws Exception {
        taskCounterCB1 = 5;
        taskCounterCB2 = 5;
        taskCounterCB3 = 5;
        taskCounterCB4 = 5;
        taskCounterCB5 = 5;

        log.info("Starte Test mit Zeitscheibe: " + zeitscheibe);
        Consumer consumer = new Consumer();
        consumer.setCurrentLoad(2);
        anschluss.addConsumer(consumer);
        chargeBox1.setCurrentLoad(11);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(11);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        // 1. Balancing
        chargeBox4.setCurrentLoad(11);
        chargeBox4.setCharging(true);
        strategy.addLV(chargeBox4);
        anschluss.computeLoad();
        log();
        incCounter();

        consumer.setCurrentLoad(9);

        // 2. Balancing
        chargeBox5.setCurrentLoad(11);
        chargeBox5.setCharging(true);
        strategy.addLV(chargeBox5);
        anschluss.computeLoad();
        log();
        incCounter();

        while (!strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge(zeitscheibe);
            if (zeitpunkt == 10) {
                consumer.setCurrentLoad(4);
            }
        }
        printSchedule();
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }

}
