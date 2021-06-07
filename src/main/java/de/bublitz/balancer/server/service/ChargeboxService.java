package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.ChargeBox;

import java.util.List;

public interface ChargeboxService {
    boolean addChargeBox(ChargeBox chargeBox);

    List<ChargeBox> getAllChargeBox();

    ChargeBox getChargeBoxByName(String name);

    ChargeBox getChargeboxById(String evseid);

    ChargeBox getChargeboxById(long id);

    void deleteChargeBox(long id);

    void deleteChargeBox(String evseid);

    void setCharging(String name, boolean active);

    void setConnected(String name);

    void calibrate();

    void update(ChargeBox chargeBox);

    boolean exists(long id);

    boolean exists(String evseid);
}
