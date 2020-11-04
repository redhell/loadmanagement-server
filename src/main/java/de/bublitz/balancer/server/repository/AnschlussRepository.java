package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.Anschluss;
import org.springframework.data.repository.CrudRepository;

public interface AnschlussRepository extends CrudRepository<Anschluss, Long> {
    public Anschluss getAnschlussByName(String name);
}
