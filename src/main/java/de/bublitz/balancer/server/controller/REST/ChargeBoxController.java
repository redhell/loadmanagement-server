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
                                @RequestParam String evseid,
                                @RequestParam String startURL,
                                @RequestParam String stopURL) {
        ChargeBox chargeBox = new ChargeBox();
        chargeBox.setName(name);
        chargeBox.setStartURL(startURL);
        chargeBox.setStopURL(stopURL);
        chargeBox.setEvseid(evseid);
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
    public ChargeBox getChargeBoxById(@RequestParam String evseid) {
        return chargeboxRepository.getChargeBoxByEvseid(evseid);
    }

    @GetMapping("/remove")
    public void deleteChargeBox(@RequestParam String name, HttpServletResponse response) {
        ChargeBox delChargeBox = chargeboxRepository.getChargeBoxByName(name);
        if(delChargeBox != null) {
            chargeboxRepository.deleteById(delChargeBox.getChargeboxId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chargebox not found");
        }
    }

}