package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.ChargeBox;
import org.springframework.data.repository.CrudRepository;

public interface ChargeboxRepository extends CrudRepository<ChargeBox, Long> {

    ChargeBox getChargeBoxByName(String name);
    ChargeBox getChargeBoxByChargeboxID(String chargeboxID);
}
