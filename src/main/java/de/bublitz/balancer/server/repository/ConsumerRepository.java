package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Consumer getConsumerByName(String name);

    void deleteConsumerByName(String name);

    boolean existsByName(String name);
}
