package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.Anschluss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnschlussRepository extends JpaRepository<Anschluss, Long> {
    Anschluss getAnschlussByName(String name);

    Boolean existsAnschlussByName(String name);
}
