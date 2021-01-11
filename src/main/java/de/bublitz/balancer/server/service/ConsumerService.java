package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.Consumer;

import java.util.List;

public interface ConsumerService {
    void addConsumer(String name);

    void deleteConsumer(String name);

    List<Consumer> getAllConsumers();

    void putCurrentLoad(String name, double currentLoad);

    Consumer getConsumerByName(String name);
}
