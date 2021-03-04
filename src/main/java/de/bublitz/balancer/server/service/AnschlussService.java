package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;

import java.util.List;

public interface AnschlussService {

    Anschluss getAnschlussByName(String name);

    void addAnschluss(Anschluss anschluss);

    List<Anschluss> getAll();

    boolean removeAnschluss(String name);

    void addChargeboxToAnschluss(String anschlussName, String chargeboxName);

    void addConsumerToAnschluss(String anschlussName, String consumerName);

    void addChargeboxToAnschluss(ChargeBox chargeBox);

    void addConsumerToAnschluss(Consumer consumer);

    Anschluss getAnschlussById(long id);

    void update(Anschluss anschluss);

    void deleteAnschluss(long id);
}
