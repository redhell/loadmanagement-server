package de.bublitz.balancer.server.components.strategien;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirstComeFirstServerStrategy extends Strategy {

    public FirstComeFirstServerStrategy(Anschluss anschluss) {
        super(anschluss, LoadStrategy.FCFS);
    }

    @Override
    public void optimize() {

    }

    @Override
    public void addLV(ChargeBox chargeBox) {

    }
}
