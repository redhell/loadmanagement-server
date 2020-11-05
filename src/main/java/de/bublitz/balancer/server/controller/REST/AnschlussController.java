package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.repository.AnschlussRepository;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import de.bublitz.balancer.server.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping(value = "/anschluss")
public class AnschlussController {

    @Autowired
    private AnschlussRepository anschlussRepository;
    @Autowired
    private ChargeboxRepository chargeboxRepository;
    @Autowired
    private ConsumerRepository consumerRepository;

    @GetMapping("/add")
    public boolean addAnschluss(@RequestParam String name, @RequestParam double maxLoad) {
        Anschluss anschluss = new Anschluss();
        anschluss.setName(name);
        anschluss.setMaxLoad(maxLoad);
        anschlussRepository.save(anschluss);
        return true;
    }

    @PostMapping("/add")
    public boolean addChargeBox(@RequestBody Anschluss anschluss) {
        anschlussRepository.save(anschluss);
        return true;
    }

    @GetMapping("/getAll")
    public Iterable<Anschluss> getAllChargeBox() {
        return anschlussRepository.findAll();
    }

    @GetMapping("/getByName")
    public Anschluss getChargeBoxByName(@RequestParam String name) {
        return anschlussRepository.getAnschlussByName(name);
    }

    @GetMapping("/remove")
    public void deleteChargeBox(@RequestParam String name, HttpServletResponse response) {
        Anschluss delAnschluss = anschlussRepository.getAnschlussByName(name);
        if (delAnschluss != null) {
            anschlussRepository.deleteById(delAnschluss.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumer not found");
        }
    }

    @GetMapping("/addChargebox")
    public void addChargeboxToAnschluss(@RequestParam String anschlussName, @RequestParam String chargeboxName) {
        Anschluss anschluss = anschlussRepository.getAnschlussByName(anschlussName);
        ChargeBox chargeBox = chargeboxRepository.getChargeBoxByName(chargeboxName);
        anschluss.addChargeBox(chargeBox);

        anschlussRepository.save(anschluss);
    }

    @GetMapping("/addConsumer")
    public void addConsumerToAnschluss(@RequestParam String anschlussName, @RequestParam String consumerName) {
        Anschluss anschluss = anschlussRepository.getAnschlussByName(anschlussName);
        Consumer consumer = consumerRepository.getConsumerByName(consumerName);
        anschluss.addConsumer(consumer);

        anschlussRepository.save(anschluss);
    }
}
