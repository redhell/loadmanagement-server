package de.bublitz.balancer.server.components;

import de.bublitz.balancer.server.components.strategien.FirstComeFirstServerStrategy;
import de.bublitz.balancer.server.components.strategien.FirstInFirstOutStrategy;
import de.bublitz.balancer.server.components.strategien.PriorityQueueStrategy;
import de.bublitz.balancer.server.components.strategien.Strategy;
import de.bublitz.balancer.server.controller.InfluxController;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.model.ConsumptionPoint;
import de.bublitz.balancer.server.repository.AnschlussRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
@Transactional
public class BalancerComponent {

    private final AnschlussRepository anschlussRepository;
    private final InfluxController influxController;
    private final Map<Anschluss, Strategy> anschlussStrategyMap;

    @Autowired
    public BalancerComponent(AnschlussRepository anschlussRepository, InfluxController influxController) {
        this.anschlussRepository = anschlussRepository;
        this.influxController = influxController;
        this.anschlussStrategyMap = new LinkedHashMap<>();
    }

    @Scheduled(fixedRate = 15000, initialDelay = 2000)
    public void triggerBalance() {
        anschlussRepository.findAll().forEach(this::balance);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 5000)
    public void triggerCheckConnected() {
        anschlussRepository.findAll().forEach(this::checkConnected);
    }

    protected void checkConnected(Anschluss anschluss) {
        anschluss
                .getChargeboxList()
                .stream()
                .filter(ChargeBox::isConnected)
                .forEach(chargeBox -> {
                    ConsumptionPoint consumptionPoint
                            = influxController.getLastPoint(chargeBox.getName());
                    Duration duration = Duration.between(consumptionPoint.getTime(), Instant.now());

                    if (duration.toMinutesPart() > 10) {
                        chargeBox.setConnected(false);
                        chargeBox.setCurrentLoad(0);
                        log.warn(chargeBox.getName() + " is not connected!");
                    } else {
                        log.debug(chargeBox.getName() + " is still connected!");
                    }
                });
    }

    protected void balance(Anschluss anschluss) {
        Strategy strategy;
        if (anschlussStrategyMap.containsKey(anschluss)) {
            strategy = anschlussStrategyMap.get(anschluss);
            // Update
            strategy.setAnschluss(anschluss);
            strategy.optimize();
        } else {

            switch (anschluss.getLoadStrategy()) {
                case PQ:
                    strategy = new PriorityQueueStrategy(anschluss);
                    break;
                case FCFS:
                    strategy = new FirstComeFirstServerStrategy(anschluss);
                    break;
                case FIFO:
                default:
                    strategy = new FirstInFirstOutStrategy(anschluss);
            }
            anschlussStrategyMap.put(anschluss, strategy);
        }
        // Calculate load
        anschluss.getChargeboxList().forEach(this::updateChargeBox);
        anschluss.getConsumerList().forEach(this::updateConsumer);
        anschluss.computeLoad();
        log.info("Current load of " + anschluss.getName() + ": " + anschluss.getCurrentLoad() + "A");
    }

    private void updateConsumer(Consumer consumer) {
        consumer.setCurrentLoad(influxController.getLastPoint(consumer.getName()).getConsumption());
    }

    private void updateChargeBox(ChargeBox chargeBox) {
        Strategy strategy = anschlussStrategyMap.get(chargeBox.getAnschluss());
        if (chargeBox.isConnected()) {
            Optional<ConsumptionPoint> consumption =
                    Optional.ofNullable(influxController.getLastPoint(chargeBox.getName()));
            if (consumption.isPresent()) {
                chargeBox.setCurrentLoad(consumption.get().getConsumption());
                // 20% Buffer
                if (chargeBox.getCurrentLoad() > chargeBox.getIdleConsumption() * 1.1) {
                    if (!chargeBox.isCharging()) {
                        // not charging -> charging
                        log.info("Start charging");
                        strategy.addLV(chargeBox);
                        chargeBox.setCharging(true);
                    }
                } else {
                    if (strategy.getChargingList().contains(chargeBox)) {
                        chargeBox.setCharging(false);
                        strategy.removeLV(chargeBox);
                        log.info("Stop charging");
                    } else if (strategy.getSuspended().isEmpty()) {
                        chargeBox.setCharging(false);
                    }
                }

                log.info("Current load of " + chargeBox.getName() + ": " + chargeBox.getCurrentLoad() + "A");
            } else {
                chargeBox.setCurrentLoad(0.0);
            }
        } else {
            log.debug(chargeBox.getName() + " is not connected");
        }
    }
}
