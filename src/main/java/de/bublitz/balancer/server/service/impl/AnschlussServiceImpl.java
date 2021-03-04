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

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Transactional
public class AnschlussServiceImpl implements AnschlussService {
    private final AnschlussRepository anschlussRepository;
    private final ChargeboxRepository chargeboxRepository;
    private final ConsumerRepository consumerRepository;

    @Autowired
    public AnschlussServiceImpl(AnschlussRepository anschlussRepository, ChargeboxRepository chargeboxRepository, ConsumerRepository consumerRepository) {
        this.anschlussRepository = anschlussRepository;
        this.chargeboxRepository = chargeboxRepository;
        this.consumerRepository = consumerRepository;
    }

    @PostConstruct
    public void createDefault() {
        if (!anschlussRepository.existsById(1L))
            addAnschluss(new Anschluss());
    }

    @Override
    public Anschluss getAnschlussByName(String name) {
        return anschlussRepository.getAnschlussByName(name);
    }

    @Override
    public void addAnschluss(Anschluss anschluss) {
        if (!anschlussRepository.existsAnschlussByName(anschluss.getName())) {
            anschlussRepository.save(anschluss);
        }
    }

    @Override
    public List<Anschluss> getAll() {
        return anschlussRepository.findAll();
    }

    @Override
    public boolean removeAnschluss(String name) {
        Anschluss delAnschluss = anschlussRepository.getAnschlussByName(name);
        if (delAnschluss != null) {
            anschlussRepository.delete(delAnschluss);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addChargeboxToAnschluss(String anschlussName, String chargeboxName) {
        Anschluss anschluss = anschlussRepository.getAnschlussByName(anschlussName);
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(chargeboxName);
        anschluss.addChargeBox(chargeBox);
    }

    @Override
    public void addConsumerToAnschluss(String anschlussName, String consumerName) {
        Anschluss anschluss = anschlussRepository.getAnschlussByName(anschlussName);
        Consumer consumer = consumerRepository.getConsumerByName(consumerName);
        anschluss.addConsumer(consumer);
    }

    @Override
    public void addChargeboxToAnschluss(ChargeBox chargeBox) {
        Anschluss anschluss = anschlussRepository.getOne(1L);
        anschluss.addChargeBox(chargeBox);
    }

    @Override
    public void addConsumerToAnschluss(Consumer consumer) {
        Anschluss anschluss = anschlussRepository.getOne(1L);
        anschluss.addConsumer(consumer);
    }

    @Override
    public Anschluss getAnschlussById(long id) {
        return anschlussRepository.getOne(id);
    }

    @Override
    public void update(Anschluss anschluss) {
        Anschluss oldAnschluss = anschlussRepository.getOne(anschluss.getId());
        oldAnschluss.setName(anschluss.getName());
        oldAnschluss.setMaxLoad(anschluss.getMaxLoad());
        oldAnschluss.setHardLimit(anschluss.getHardLimit());
        oldAnschluss.setSoftLimit(anschluss.getSoftLimit());
    }

    @Override
    public void deleteAnschluss(long id) {
        anschlussRepository.deleteById(id);
    }
}
