package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.service.AnschlussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;


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
    public Iterable<Anschluss> getAllChargeBox() {
        return anschlussService.getAll();
    }

    @GetMapping("/getByName")
    public Anschluss getChargeBoxByName(@RequestParam String name) {
        return anschlussService.getAnschlussByName(name);
    }

    @DeleteMapping("/remove")
    public void deleteAnschluss(@RequestParam String name, HttpServletResponse response) {
        if (!anschlussService.removeAnschluss(name)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumer not found");
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
}
