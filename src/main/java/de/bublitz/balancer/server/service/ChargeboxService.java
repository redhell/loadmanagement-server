package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.ChargeBox;

public interface ChargeboxService {
    void addChargeBox(ChargeBox chargeBox);

    Iterable<ChargeBox> getAllChargeBox();

    ChargeBox getChargeBoxByName(String name);

    ChargeBox getChargeboxById(String evseid);

    ChargeBox getChargeboxById(long id);

    void deleteChargeBox(long id);

    void setCharging(String name, boolean active);

    void setConnected(String name);

    void calibrate();

    void setListener(String name);

    void update(ChargeBox chargeBox);

    boolean exists(long id);
}
