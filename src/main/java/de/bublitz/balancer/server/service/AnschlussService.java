package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.Anschluss;

public interface AnschlussService {

    Anschluss getAnschlussByName(String name);

    void addAnschluss(Anschluss anschluss);

    Iterable<Anschluss> getAll();

    boolean removeAnschluss(String name);

    void addChargeboxToAnschluss(String anschlussName, String chargeboxName);

    void addConsumerToAnschluss(String anschlussName, String consumerName);

    void removeChargebox(String anschlussName, String chargeboxName);

    void removeConsumer(String anschlussName, String consumerName);

    void updateAnschluss(Anschluss anschluss);
}
