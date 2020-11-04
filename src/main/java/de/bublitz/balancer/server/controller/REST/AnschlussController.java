package de.bublitz.balancer.server.controller.REST;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.repository.AnschlussRepository;
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

    @GetMapping("/add")
    public boolean addAnschluss(@RequestParam String name) {
        Anschluss anschluss = new Anschluss();
        anschluss.setName(name);
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
}
