package de.bublitz.balancer.server.controller.strategies;

import de.bublitz.balancer.server.model.ChargeBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Slf4j
public class LastInFirstOutStrategy extends Strategy {

    @Scheduled(fixedRate = 5000)
    public void balance() {
        log.info("Balance");
        if (isHardlimitReached()) {
            ChargeBox cb = this.getChargingBoxes().get(getChargingBoxes().size() - 1);
            sendStopCharging(cb);
        }
    }
}
