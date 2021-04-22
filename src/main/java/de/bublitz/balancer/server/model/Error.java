package de.bublitz.balancer.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Entity
@Data
public class Error {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private LocalDateTime timestamp;
    private String stacktrace;
    private String type;

    public Error(Exception ex) {
        message = ex.getMessage();
        timestamp = LocalDateTime.now();
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        stacktrace = stringWriter.toString();
        type = ex.getClass().getSimpleName();
    }

    public Error() {

    }
}
