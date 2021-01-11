package de.bublitz.balancer.server.service.impl;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.repository.AnschlussRepository;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import de.bublitz.balancer.server.repository.ConsumerRepository;
import de.bublitz.balancer.server.service.AnschlussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnschlussServiceImpl implements AnschlussService {
    @Autowired
    private AnschlussRepository anschlussRepository;
    @Autowired
    private ChargeboxRepository chargeboxRepository;
    @Autowired
    private ConsumerRepository consumerRepository;

    @Transactional
    public Anschluss getAnschlussByName(String name) {
        return anschlussRepository.getAnschlussByName(name);
    }

    @Transactional
    public void addAnschluss(Anschluss anschluss) {
        anschlussRepository.save(anschluss);
    }

    @Transactional
    public Iterable<Anschluss> getAll() {
        return anschlussRepository.findAll();
    }

    @Transactional
    public boolean removeAnschluss(String name) {
        Anschluss delAnschluss = anschlussRepository.getAnschlussByName(name);
        if (delAnschluss != null) {
            anschlussRepository.delete(delAnschluss);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void addChargeboxToAnschluss(String anschlussName, String chargeboxName) {
        Anschluss anschluss = anschlussRepository.getAnschlussByName(anschlussName);
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(chargeboxName);
        anschluss.addChargeBox(chargeBox);

        anschlussRepository.save(anschluss);
    }

    @Transactional
    public void addConsumerToAnschluss(String anschlussName, String consumerName) {
        Anschluss anschluss = anschlussRepository.getAnschlussByName(anschlussName);
        Consumer consumer = consumerRepository.getConsumerByName(consumerName);
        anschluss.addConsumer(consumer);

        anschlussRepository.save(anschluss);
    }
}
