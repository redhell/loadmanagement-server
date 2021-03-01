package de.bublitz.balancer.server.controller.REST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bublitz.balancer.server.controller.InfluxController;
import de.bublitz.balancer.server.model.ConsumptionPoint;
import de.bublitz.balancer.server.service.ChargeboxService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/load")
public class LoadController {
    @Autowired
    private InfluxController influxController;

    @Autowired
    private ChargeboxService chargeboxService;

    @GetMapping("/getAll")
    public List<ConsumptionPoint> getAllPoints() {
        return influxController.getAllPoints();
    }

    @PostMapping(value = "/addPoint", consumes = "application/json")
    public void addPoint(@RequestBody ConsumptionPoint consumptionPoint) {
        influxController.addPoint(consumptionPoint);
    }

    @PostMapping(value = "/addPoints", consumes = "application/json")
    public void addPoints(@RequestBody List<ConsumptionPoint> consumptionPoints) {
        influxController.addPoints(consumptionPoints);
    }

    @PostMapping("/{name}/rawPoints")
    public void addRawPoints(@PathVariable String name, @RequestBody Map<LocalDateTime, String> pointMap) {
        chargeboxService.setConnected(name);
        chargeboxService.setListener(name);
        List<ConsumptionPoint> tmpList = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<LinkedHashMap<String, String>> typeRef = new TypeReference<>() {
        };
        pointMap.forEach((key, value) -> {
            try {
                Map<String, String> tmpMap = mapper.readValue(value, typeRef);
                ConsumptionPoint consumptionPoint = new ConsumptionPoint(name,
                        Double.parseDouble(tmpMap.get("I")),
                        key.atZone(ZoneId.of("Europe/Berlin")).toInstant(),
                        tmpMap.get("unit"));
                tmpList.add(consumptionPoint);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        });
        influxController.addPoints(tmpList);

        // Noch nicht kalibriert für Idle!
        chargeboxService.calibrate();
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
