package de.bublitz.balancer.server.service.impl;

import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import de.bublitz.balancer.server.service.ChargeboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChargeboxServiceImpl implements ChargeboxService {

    @Autowired
    private ChargeboxRepository chargeboxRepository;

    @Transactional
    public void addChargeBox(ChargeBox chargeBox) {
        if (!chargeboxRepository.existsChargeBoxByName(chargeBox.getName())) {
            chargeboxRepository.save(chargeBox);
        }
    }

    @Transactional
    public Iterable<ChargeBox> getAllChargeBox() {
        return chargeboxRepository.findAll();
    }

    @Transactional
    public ChargeBox getChargeBoxByName(String name) {
        return chargeboxRepository.getChargeBoxByName(name);
    }

    @Transactional
    public ChargeBox getChargeBoxById(String evseid) {
        return chargeboxRepository.getChargeBoxByEvseid(evseid);
    }

    @Transactional
    public boolean deleteChargeBox(String name) {
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(name);
        if (chargeBox != null) {
            chargeboxRepository.delete(chargeBox);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void setCharging(String name, boolean active) {
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(name);
        chargeBox.setCharging(active);
        chargeboxRepository.save(chargeBox);
    }
}
