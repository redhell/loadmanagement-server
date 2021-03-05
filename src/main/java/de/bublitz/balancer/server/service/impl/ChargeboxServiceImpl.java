package de.bublitz.balancer.server.service.impl;

import de.bublitz.balancer.server.components.BalancerComponent;
import de.bublitz.balancer.server.controller.InfluxController;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.ConsumptionPoint;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import de.bublitz.balancer.server.service.ChargeboxService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;

@Service
@Log4j2
@Transactional
public class ChargeboxServiceImpl implements ChargeboxService {

    private final ChargeboxRepository chargeboxRepository;
    private final InfluxController influxController;

    @Autowired
    public ChargeboxServiceImpl(ChargeboxRepository chargeboxRepository, InfluxController influxController, BalancerComponent balancerComponent) {
        this.chargeboxRepository = chargeboxRepository;
        this.influxController = influxController;
    }

    public void addChargeBox(ChargeBox chargeBox) {
        if (!chargeboxRepository.existsChargeBoxByName(chargeBox.getName())) {
            chargeboxRepository.save(chargeBox);
        }
    }

    public Iterable<ChargeBox> getAllChargeBox() {
        return chargeboxRepository.findAll();
    }

    public ChargeBox getChargeBoxByName(String name) {
        return chargeboxRepository.getChargeBoxByName(name);
    }

    public ChargeBox getChargeboxById(String evseid) {
        return chargeboxRepository.getChargeBoxByEvseid(evseid);
    }

    @Override
    public ChargeBox getChargeboxById(long id) {
        return chargeboxRepository.getOne(id);
    }

    public void deleteChargeBox(long id) {
        ChargeBox chargeBox = chargeboxRepository.getOne(id);
        chargeboxRepository.delete(chargeBox);
    }

    public void setCharging(String name, boolean active) {
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(name);
        chargeBox.setCharging(active);
        chargeboxRepository.save(chargeBox);
    }

    @Override
    public void setConnected(String name) {
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(name);
        if (!chargeBox.isConnected()) {
            chargeBox.setConnected(true);
            chargeboxRepository.save(chargeBox);
            log.debug(name + " is now connected");
        } else {
            log.debug(name + " is already connected");
        }
    }

    /**
     * Calibrates idle consumption
     */
    @Override
    public void calibrate() {
        // 0.0 is default -> uncalibrated
        List<ChargeBox> chargeBoxes = chargeboxRepository.findChargeBoxesByCalibratedFalseAndConnectedTrue();
        chargeBoxes.forEach(cb -> {
            List<ConsumptionPoint> loadData = influxController.getPointsByName(cb.getName());
            double tmpValues = 0.0;
            for (ConsumptionPoint loadDatum : loadData) {
                tmpValues += loadDatum.getConsumption();
            }
            cb.setIdleConsumption((tmpValues / loadData.size()) * 1.3);
            cb.setCalibrated(true);
            log.debug("Calculated ilde of " + cb.getName() + ": " + new DecimalFormat("#.###").format(cb.getIdleConsumption()) + "A");
        });
    }

    @Override
    public void setListener(String name) {
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(name);
    }

    @Override
    public void update(ChargeBox chargeBox) {
        ChargeBox oldChargebox = chargeboxRepository.getChargeBoxByEvseid(chargeBox.getEvseid());
        oldChargebox.setName(chargeBox.getName());
        oldChargebox.setIdleConsumption(chargeBox.getIdleConsumption());
        oldChargebox.setCharging(chargeBox.isCharging());
        oldChargebox.setConnected(chargeBox.isConnected());
        oldChargebox.setEmaid(chargeBox.getEmaid());
        oldChargebox.setCalibrated(chargeBox.isCalibrated());
        oldChargebox.setPriority(chargeBox.isPriority());
        oldChargebox.setStartURL(chargeBox.getStartURL());
        oldChargebox.setStopURL(chargeBox.getStopURL());
        oldChargebox.setEvseid(chargeBox.getEvseid());
        oldChargebox.setAnschluss(chargeBox.getAnschluss());
    }

    @Override
    public boolean exists(long id) {
        return chargeboxRepository.existsById(id);
    }
}
