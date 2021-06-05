package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping(value = "/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @PostMapping("/add")
    public boolean addConsumerPost(@RequestBody Consumer consumer) {
        consumerService.addConsumer(consumer);
        return true;
    }

    @GetMapping("/add")
    public boolean addConsumerGet(@RequestParam String name) {
        consumerService.addConsumer(name);
        return true;
    }


    @GetMapping("/getAll")
    public List<Consumer> getAllConsumers() {
        return consumerService.getAllConsumers();
    }

    @DeleteMapping("/remove/id/{id}")
    public void deleteConsumer(@PathVariable Long id) {
        consumerService.deleteConsumer(id);
    }

    @DeleteMapping("/remove/name/{name}")
    public void deleteConsumerByName(@PathVariable String name) {
        consumerService.deleteConsumer(name);
    }

    @PutMapping("/{name}/putCurrentLoad")
    public void putCurrentLoad(@PathVariable String name, @RequestBody double currentLoad) {
        consumerService.putCurrentLoad(name, currentLoad);
    }

    @GetMapping("/{name}/details")
    public Consumer getConsumerDetails(@PathVariable String name) {
        return consumerService.getConsumerByName(name);
    }

    @GetMapping("/updateConsumer")
    public void updateConsumer(@RequestBody Consumer consumer) {
        consumerService.update(consumer);
    }
}
