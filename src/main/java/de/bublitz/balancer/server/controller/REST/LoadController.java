package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.controller.InfluxController;
import de.bublitz.balancer.server.model.ConsumptionPoint;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/load")
public class LoadController {

    InfluxController influxController = new InfluxController();

    @GetMapping("/getAll")
    public List<ConsumptionPoint> getAllPoints() {
        return influxController.getAllPoints();
    }

    @PostMapping(value ="/addPoint", consumes = "application/json")
    public void addPoint(@RequestBody ConsumptionPoint consumptionPoint) {
        influxController.addPoint(consumptionPoint);
    }

    @PostMapping(value ="/addPoints", consumes = "application/json")
    public void addPoints(@RequestBody List<ConsumptionPoint> consumptionPoints) {
        influxController.addPoints(consumptionPoints);
    }

    @GetMapping("/getByName")
    public List<ConsumptionPoint> getPointsByName(@RequestParam String name) {
        return influxController.getPointsByName(name);
    }

    @GetMapping("/recreateDB")
    public void recreateDB() {
        influxController.recreateDatabase();
    }

    @DeleteMapping("/delete")
    public void deleteByName(@RequestParam String name) {
        influxController.delete(name);
    }
}
