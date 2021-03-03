package de.bublitz.balancer.server.model.exception;

import java.io.IOException;

public class NotStoppedException extends IOException {
    public NotStoppedException() {
        super("Charging Session couldn't be stopped!");
    }
}
