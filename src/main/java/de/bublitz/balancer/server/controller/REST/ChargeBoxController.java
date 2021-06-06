package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/chargebox")
public class ChargeBoxController {

    private final ChargeboxService chargeboxService;
    private final AnschlussService anschlussService;

    @Autowired
    public ChargeBoxController(ChargeboxService chargeboxService, AnschlussService anschlussService) {
        this.chargeboxService = chargeboxService;
        this.anschlussService = anschlussService;
    }

    @GetMapping("/add")
    public boolean addChargeBox(@RequestParam String name,
                                @RequestParam String evseid,
                                @RequestParam String startURL,
                                @RequestParam String stopURL) {
        ChargeBox chargeBox = new ChargeBox();
        chargeBox.setName(name);
        chargeBox.setStartURL(startURL);
        chargeBox.setStopURL(stopURL);
        chargeBox.setEvseid(evseid);
        chargeboxService.addChargeBox(chargeBox);
        return anschlussService.addChargeboxToAnschluss(chargeBox);
    }

    @PostMapping("/add")
    public boolean addChargeBox(@RequestBody ChargeBox pChargebox) {
        //chargeboxService.addChargeBox(pChargebox);
        return anschlussService.addChargeboxToAnschluss(pChargebox);
    }

    @GetMapping("/getAll")
    public List<ChargeBox> getAllChargeBox() {
        return chargeboxService.getAllChargeBox();
    }

    @GetMapping("/getByName")
    public ChargeBox getChargeBoxByName(@RequestParam String name) {
        return chargeboxService.getChargeBoxByName(name);
    }

    @GetMapping("/getByEvseId")
    public ChargeBox getChargeBoxById(@RequestParam String evseid) {
        return chargeboxService.getChargeboxById(evseid);
    }

    @DeleteMapping("/remove/id/{id}")
    public void deleteChargeBox(@PathVariable long id) {
        if (!chargeboxService.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chargebox not found");
        } else {
            chargeboxService.deleteChargeBox(id);
        }
    }

    @DeleteMapping("/remove/evseid/{evseid}")
    public void deleteChargeBoxByEvseid(@PathVariable String evseid) {
        if (!chargeboxService.exists(evseid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chargebox not found");
        } else {
            chargeboxService.deleteChargeBox(evseid);
        }
    }

    @GetMapping("/startCharging")
    public void startCharging(@RequestParam String name) {
        chargeboxService.setCharging(name, true);
    }

    @GetMapping("/stopCharging")
    public void stopCharging(@RequestParam String name) {
        chargeboxService.setCharging(name, false);
    }

    @PostMapping("/update")
    public void updateChargebox(@RequestBody ChargeBox chargeBox) {
        chargeboxService.update(chargeBox);
    }
}