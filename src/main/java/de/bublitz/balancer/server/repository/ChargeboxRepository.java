package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.ChargeBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeboxRepository extends JpaRepository<ChargeBox, Long> {

    ChargeBox getChargeBoxByName(String name);

    ChargeBox getChargeBoxByEvseid(String evseid);

    boolean existsChargeBoxByName(String name);

}
