package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping(value = "/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerRepository consumerRepository;

    @PostMapping("/add")
    public boolean addConsumer(@RequestParam String name) {
        Consumer consumer = new Consumer();
        consumer.setName(name);
        consumerRepository.save(consumer);
        return true;
    }

    @GetMapping("/getAll")
    public Iterable<Consumer> getAllConsumers() {
        return consumerRepository.findAll();
    }

    @GetMapping("/remove")
    public void deleteConsumer(@RequestParam String name, HttpServletResponse response) {
        Consumer delConsumer = consumerRepository.getConsumerByName(name);
        if(delConsumer != null) {
            consumerRepository.deleteById(delConsumer.getId());
        } else {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumer not found");
        }
    }
}
