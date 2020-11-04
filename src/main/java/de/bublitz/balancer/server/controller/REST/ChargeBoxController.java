package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping(value = "/chargebox")
public class ChargeBoxController {

    @Autowired
    private ChargeboxRepository chargeboxRepository;

    @GetMapping("/add")
    public boolean addChargeBox(@RequestParam String name,
                                @RequestParam String chargeboxID,
                                @RequestParam String startURL,
                                @RequestParam String stopURL) {
        ChargeBox chargeBox = new ChargeBox();
        chargeBox.setName(name);
        chargeBox.setStartURL(startURL);
        chargeBox.setStopURL(stopURL);
        chargeBox.setChargeboxID(chargeboxID);
        chargeboxRepository.save(chargeBox);
        return true;
    }

    @PostMapping("/add")
    public boolean addChargeBox(@RequestBody ChargeBox pChargebox) {
        chargeboxRepository.save(pChargebox);
        return true;
    }

    @GetMapping("/getAll")
    public Iterable<ChargeBox> getAllChargeBox() {
        return chargeboxRepository.findAll();
    }

    @GetMapping("/getByName")
    public ChargeBox getChargeBoxByName(@RequestParam String name) {
        return chargeboxRepository.getChargeBoxByName(name);
    }

    @GetMapping("/getById")
    public ChargeBox getChargeBoxById(@RequestParam String id) {
        return chargeboxRepository.getChargeBoxByChargeboxID(id);
    }

    @GetMapping("/remove")
    public void deleteChargeBox(@RequestParam String name, HttpServletResponse response) {
        ChargeBox delChargeBox = chargeboxRepository.getChargeBoxByName(name);
        if(delChargeBox != null) {
            chargeboxRepository.deleteById(delChargeBox.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumer not found");
        }
    }

}