package de.bublitz.balancer.server.service.impl;

import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.repository.ConsumerRepository;
import de.bublitz.balancer.server.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerSerciceImpl implements ConsumerService {
    @Autowired
    private ConsumerRepository consumerRepository;

    @Override
    public void addConsumer(String name) {
        Consumer consumer = new Consumer();
        consumer.setName(name);
        consumerRepository.save(consumer);
    }

    @Override
    public void deleteConsumer(String name) {
        consumerRepository.deleteConsumerByName(name);
    }

    @Override
    public List<Consumer> getAllConsumers() {
        return consumerRepository.findAll();
    }

    @Override
    public void putCurrentLoad(String name, double currentLoad) {
        Consumer consumer = consumerRepository.getConsumerByName(name);
        consumer.setCurrentLoad(currentLoad);
    }

    @Override
    public Consumer getConsumerByName(String name) {
        return consumerRepository.getConsumerByName(name);
    }

}