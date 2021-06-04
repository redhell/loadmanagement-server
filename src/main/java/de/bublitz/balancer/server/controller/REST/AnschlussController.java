package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.service.AnschlussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping(value = "/anschluss")
public class AnschlussController {

    @Autowired
    private AnschlussService anschlussService;


    @GetMapping("/add")
    public boolean addAnschluss(@RequestParam String name, @RequestParam double maxLoad) {
        Anschluss anschluss = new Anschluss();
        anschluss.setName(name);
        anschluss.setMaxLoad(maxLoad);
        anschlussService.addAnschluss(anschluss);
        return true;
    }

    @PostMapping("/add")
    public boolean addChargeBox(@RequestBody Anschluss anschluss) {
        anschlussService.addAnschluss(anschluss);
        return true;
    }

    @GetMapping("/getAll")
    public List<Anschluss> getAllChargeBox() {
        return anschlussService.getAll();
    }

    @GetMapping("/getByName")
    public Anschluss getChargeBoxByName(@RequestParam String name) {
        return anschlussService.getAnschlussByName(name);
    }

    @DeleteMapping("/remove")
    public void deleteAnschluss(@RequestParam String name, HttpServletResponse response) {
        if (!anschlussService.removeAnschluss(name)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Anschluss not found");
        }
    }

    @GetMapping("/addChargebox")
    public void addChargeboxToAnschluss(@RequestParam String anschlussName, @RequestParam String chargeboxName) {
        anschlussService.addChargeboxToAnschluss(anschlussName, chargeboxName);
    }

    @GetMapping("/addConsumer")
    public void addConsumerToAnschluss(@RequestParam String anschlussName, @RequestParam String consumerName) {
        anschlussService.addConsumerToAnschluss(anschlussName, consumerName);
    }

    @GetMapping("/removeConsumer")
    public void removeConsumer(@RequestParam String anschlussName, @RequestParam String consumerName) {
        anschlussService.removeConsumer(anschlussName, consumerName);
    }

    @GetMapping("/removeChargebox")
    public void removeChargebox(@RequestParam String anschlussName, @RequestParam String chargeboxName) {
        anschlussService.removeChargebox(anschlussName, chargeboxName);
    }

    @PostMapping("/updateAnschluss")
    public void updateAnschluss(@RequestBody Anschluss anschluss) {
        anschlussService.updateAnschluss(anschluss);
    }
}
