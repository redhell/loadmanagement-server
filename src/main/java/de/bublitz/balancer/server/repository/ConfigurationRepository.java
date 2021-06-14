package de.bublitz.balancer.server.repository;

import de.bublitz.balancer.server.model.ConfigurationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationItem, Long> {
    ConfigurationItem getByConfigKey(String key);

    boolean existsByConfigKey(String key);
}
