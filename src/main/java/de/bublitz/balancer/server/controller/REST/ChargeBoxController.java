package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        anschlussService.addChargeboxToAnschluss(chargeBox);
        return true;
    }

    @PostMapping("/add")
    public boolean addChargeBox(@RequestBody ChargeBox pChargebox) {
        chargeboxService.addChargeBox(pChargebox);
        anschlussService.addChargeboxToAnschluss(pChargebox);
        return true;
    }

    @GetMapping("/getAll")
    public Iterable<ChargeBox> getAllChargeBox() {
        return chargeboxService.getAllChargeBox();
    }

    @GetMapping("/getByName")
    public ChargeBox getChargeBoxByName(@RequestParam String name) {
        return chargeboxService.getChargeBoxByName(name);
    }

    @GetMapping("/getById")
    public ChargeBox getChargeBoxById(@RequestParam String evseid) {
        return chargeboxService.getChargeBoxById(evseid);
    }

    @DeleteMapping("/remove")
    public void deleteChargeBox(@RequestParam long id) {
        chargeboxService.deleteChargeBox(id);
        if (!chargeboxService.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chargebox not found");
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
}