package de.bublitz.balancer.server.controller.REST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bublitz.balancer.server.model.ConsumptionPoint;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.InfluxService;
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
    private InfluxService influxService;

    @Autowired
    private ChargeboxService chargeboxService;

    @GetMapping("/getAll")
    public List<ConsumptionPoint> getAllPoints() {
        return influxService.getAllPoints();
    }

    @PostMapping(value = "/addPoint", consumes = "application/json")
    public void addPoint(@RequestBody ConsumptionPoint consumptionPoint) {
        influxService.addPoint(consumptionPoint);
    }

    @PostMapping(value = "/addPoints", consumes = "application/json")
    public void addPoints(@RequestBody List<ConsumptionPoint> consumptionPoints) {
        influxService.addPoints(consumptionPoints);
    }

    @PostMapping("/{type}/{name}/rawPoints")
    public void addRawPoints(@PathVariable String type, @PathVariable String name, @RequestBody Map<LocalDateTime, String> pointMap) {
        if (type.equals("chargebox")) {
            chargeboxService.setConnected(name);
            processPoints(name, pointMap);
            // Noch nicht kalibriert für Idle!
            chargeboxService.calibrate();
        } else {
            processPoints(name, pointMap);
        }
    }

    private void processPoints(String name, Map<LocalDateTime, String> pointMap) {
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
        influxService.addPoints(tmpList);
    }

    @GetMapping("/getByName")
    public List<ConsumptionPoint> getPointsByName(@RequestParam String name) {
        return influxService.getPointsByName(name);
    }

    @GetMapping("/recreateDB")
    public void recreateDB() {
        influxService.recreateDatabase();
    }

    @DeleteMapping("/delete")
    public void deleteByName(@RequestParam String name) {
        influxService.delete(name);
    }
}
