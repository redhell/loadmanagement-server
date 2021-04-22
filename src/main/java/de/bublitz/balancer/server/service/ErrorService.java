package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.model.Error;

import java.util.List;

public interface ErrorService {
    void addError(Exception ex);

    void deleteAll();

    List<Error> getAllErrors();
}
