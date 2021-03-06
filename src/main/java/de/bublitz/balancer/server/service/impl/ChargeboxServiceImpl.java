package de.bublitz.balancer.server.service.impl;

import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.ConsumptionPoint;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.InfluxService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.List;

@Service
@Log4j2
@Transactional
public class ChargeboxServiceImpl implements ChargeboxService {

    private final ChargeboxRepository chargeboxRepository;
    private final InfluxService influxService;
    private final AnschlussService anschlussService;

    @Autowired
    public ChargeboxServiceImpl(ChargeboxRepository chargeboxRepository, InfluxService influxService, AnschlussService anschlussService) {
        this.chargeboxRepository = chargeboxRepository;
        this.influxService = influxService;
        this.anschlussService = anschlussService;
    }

    public boolean addChargeBox(ChargeBox chargeBox) {
        if (!chargeboxRepository.existsChargeBoxByName(chargeBox.getName())) {
            chargeboxRepository.save(chargeBox);
            anschlussService.addChargeboxToAnschluss(chargeBox);
            return true;
        }
        return false;
    }

    public List<ChargeBox> getAllChargeBox() {
        return chargeboxRepository.findAll();
    }

    @Override
    public ChargeBox getChargeBoxByName(String name) {
        return chargeboxRepository.getChargeBoxByName(name);
    }

    @Override
    public ChargeBox getChargeboxById(String evseid) {
        return chargeboxRepository.getChargeBoxByEvseid(evseid);
    }

    @Override
    public ChargeBox getChargeboxById(long id) {
        return chargeboxRepository.getById(id);
    }

    public void deleteChargeBox(long id) {
        ChargeBox chargeBox = chargeboxRepository.getById(id);
        chargeboxRepository.delete(chargeBox);
    }

    @Override
    public void deleteChargeBox(String evseid) {
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByEvseid(evseid);
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
            List<ConsumptionPoint> loadData = influxService.getPointsByName(cb.getName());
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
        oldChargebox.setVoltage(chargeBox.getVoltage());
    }

    @Override
    public boolean exists(long id) {
        return chargeboxRepository.existsById(id);
    }

    @Override
    public boolean exists(String evseid) {
        return chargeboxRepository.existsByEvseid(evseid);
    }
}
