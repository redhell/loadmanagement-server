package de.bublitz.balancer.server.components;

import de.bublitz.balancer.server.controller.InfluxController;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.repository.AnschlussRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        chargeBox.setCurrentLoad(influxController.getLastPoint(chargeBox.getName()).getConsumption());
    }
}
