package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping(value = "/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @PostMapping("/add")
    public boolean addConsumerPost(@RequestBody Consumer consumer) {
        return consumerService.addConsumer(consumer);
    }

    @GetMapping("/add")
    public boolean addConsumerGet(@RequestParam String name) {
        return consumerService.addConsumer(name);
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

    @PutMapping(value = "/{name}/putCurrentLoad", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putCurrentLoad(@PathVariable String name, @RequestBody Map<String, Double> params) {
        consumerService.putCurrentLoad(name, params.get("currentLoad"));
    }

    @GetMapping("/{name}/details")
    public Consumer getConsumerDetails(@PathVariable String name) {
        return consumerService.getConsumerByName(name);
    }

    @PostMapping("/updateConsumer")
    public void updateConsumer(@RequestBody Consumer consumer) {
        consumerService.update(consumer);
    }
}
