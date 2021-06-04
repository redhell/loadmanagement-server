package de.bublitz.balancer.server.Strategy;

import de.bublitz.balancer.server.components.strategien.PriorityQueueStrategy;
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
public class PqTest extends GeneralTest {

    @BeforeMethod
    public void SetUp() {
        prepare();
        anschluss.setLoadStrategy(LoadStrategy.PQ);
        strategy = new PriorityQueueStrategy(anschluss);

        chargeBox2.setPriority(true);
    }

    @Test
    public void onlyChargeboxesTest() throws Exception {
        log.info("Starting Basic Test!");
        chargeBox1.setCurrentLoad(4);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(7);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
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

        while (!((PriorityQueueStrategy) strategy).getPriorityQueue().isEmpty() || !strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge();
        }
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }


    @Test
    public void chargeboxesWithConsumersTest() throws Exception {
        log.info("chargeboxesWithConsumers Test");
        Consumer consumer = new Consumer();
        consumer.setCurrentLoad(2);
        anschluss.addConsumer(consumer);
        chargeBox1.setCurrentLoad(4);
        chargeBox1.setCharging(true);
        strategy.addLV(chargeBox1);
        log();
        incCounter();

        chargeBox3.setCurrentLoad(11);
        chargeBox3.setCharging(true);
        strategy.addLV(chargeBox3);
        log();
        incCounter();

        chargeBox2.setCurrentLoad(7);
        chargeBox2.setCharging(true);
        strategy.addLV(chargeBox2);
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

        while (!((PriorityQueueStrategy) strategy).getPriorityQueue().isEmpty() || !strategy.getChargingList().isEmpty() || !strategy.getSuspendedList().isEmpty()) {
            charge();
            if (zeitpunkt == 10) {
                consumer.setCurrentLoad(4);
            }
        }
        Assert.assertTrue(strategy.getChargingList().isEmpty() && strategy.getSuspendedList().isEmpty());
    }

    @Override
    protected void incCounter() {
        super.incCounter();
        ((PriorityQueueStrategy) strategy).getPriorityQueue().forEach(this::checkAndIncrease);

    }

    @Override
    protected void log() {
        log.info("Priority: " + ((PriorityQueueStrategy) strategy).printPrioryList()
                + " ChargingList: " + strategy.printChargingList()
                + " SuspendedList: " + strategy.printSuspendedList()
                + " Consumers: " + strategy.printConsumerLoad()
                + " Load: " + anschluss.getCurrentLoad()
                + " T: " + zeitpunkt);
    }
}