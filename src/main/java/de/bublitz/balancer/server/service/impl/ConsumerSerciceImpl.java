package de.bublitz.balancer.server.service.impl;

import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.repository.ConsumerRepository;
import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ConsumerSerciceImpl implements ConsumerService {
    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private AnschlussService anschlussService;

    @Override
    public void addConsumer(String name) {
        Consumer consumer = new Consumer();
        consumer.setName(name);
        anschlussService.addConsumerToAnschluss(consumer);
        //consumerRepository.save(consumer);
    }

    @Override
    public void deleteConsumer(Long id) {
        consumerRepository.deleteById(id);
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

    @Override
    public void update(Consumer consumer) {
        Consumer oldConsmer = consumerRepository.getOne(consumer.getConsumerID());
        oldConsmer.setName(consumer.getName());
        oldConsmer.setMaxLoad(consumer.getMaxLoad());
        oldConsmer.setAnschluss(consumer.getAnschluss());
    }

    @Override
    public Consumer getConsumerById(long id) {
        return consumerRepository.getOne(id);
    }

    @Override
    public void addConsumer(Consumer consumer) {
        consumerRepository.save(consumer);
    }

}
