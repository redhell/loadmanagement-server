package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.Consumer;
import org.springframework.data.repository.CrudRepository;

public interface ConsumerRepository extends CrudRepository<Consumer, Long> {
    Consumer getConsumerByName(String name);
}
