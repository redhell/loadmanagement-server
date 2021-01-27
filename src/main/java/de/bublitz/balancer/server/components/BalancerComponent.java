package de.bublitz.balancer.server.components;

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
import java.util.Optional;

@Component
@Log4j2
public class BalancerComponent {

    @Autowired
    private AnschlussRepository anschlussRepository;

    @Autowired
    private InfluxController influxController;

    @Scheduled(fixedRate = 15000, initialDelay = 2000)
    @Transactional
    public void triggerBalance() {
        anschlussRepository.findAll().forEach(this::balance);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 5000)
    @Transactional
    public void triggerCheckConnected() {
        anschlussRepository.findAll().forEach(this::checkConnected);
    }

    @Transactional
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
                        log.warn(chargeBox.getName() + " is not connected!");
                    } else {
                        log.debug(chargeBox.getName() + " is still connected!");
                    }
                });
        anschlussRepository.save(anschluss);
    }

    @Transactional
    protected void balance(Anschluss anschluss) {
        // Calculate load
        anschluss.getChargeboxList().forEach(this::updateChargeBox);
        anschluss.getConsumerList().forEach(this::updateConsumer);
        anschluss.computeLoad();
        log.info("Current load: " + anschluss.getCurrentLoad() + "A");
        anschlussRepository.save(anschluss);
    }

    private void updateConsumer(Consumer consumer) {
        consumer.setCurrentLoad(influxController.getLastPoint(consumer.getName()).getConsumption());
    }

    private void updateChargeBox(ChargeBox chargeBox) {
        if (chargeBox.isConnected()) {
            Optional<ConsumptionPoint> consumption =
                    Optional.ofNullable(influxController.getLastPoint(chargeBox.getName()));
            if (consumption.isPresent()) {
                chargeBox.setCurrentLoad(consumption.get().getConsumption());
                log.info("Current load of " + chargeBox.getName() + ": " + chargeBox.getCurrentLoad() + "A");
            } else {
                chargeBox.setCurrentLoad(0.0);
            }
        } else {
            log.debug(chargeBox.getName() + " is not connected");
        }
    }
}
