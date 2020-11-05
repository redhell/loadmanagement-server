package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.controller.strategies.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {
}
