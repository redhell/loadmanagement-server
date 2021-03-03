package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.Errors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends JpaRepository<Errors, Long> {
}
