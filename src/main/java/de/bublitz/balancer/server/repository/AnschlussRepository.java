package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.Anschluss;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnschlussRepository extends CrudRepository<Anschluss, Long> {
    Anschluss getAnschlussByName(String name);
}
