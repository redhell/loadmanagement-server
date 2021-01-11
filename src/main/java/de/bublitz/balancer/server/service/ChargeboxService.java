package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.ChargeBox;

public interface ChargeboxService {
    void addChargeBox(ChargeBox chargeBox);

    Iterable<ChargeBox> getAllChargeBox();

    ChargeBox getChargeBoxByName(String name);

    ChargeBox getChargeBoxById(String evseid);

    boolean deleteChargeBox(String name);

    void setCharging(String name, boolean active);
}
