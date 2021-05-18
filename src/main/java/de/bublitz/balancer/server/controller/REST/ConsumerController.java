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
    public boolean addConsumer(@RequestParam String name) {
        consumerService.addConsumer(name);
        return true;
    }

    @GetMapping("/getAll")
    public List<Consumer> getAllConsumers() {
        return consumerService.getAllConsumers();
    }

    @GetMapping("/remove")
    public void deleteConsumer(@RequestParam Long id) {
        consumerService.deleteConsumer(id);
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
        consumerService.updateConsumer(consumer);
    }
}
