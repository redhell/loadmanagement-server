package de.bublitz.balancer.server.controller.Web;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Random;

@Controller
public class WebOverviewController {
    @Autowired
    private AnschlussService anschlussService;
    @Autowired
    private ChargeboxService chargeboxService;
    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/anschlusse")
    public String getAnschluss(Model model) {
        model.addAttribute("anschlusse", anschlussService.getAll());
        model.addAttribute("seite", "anschlusse");
        return "overviews/anschlussOverview";
    }

    @GetMapping("/chargeboxes")
    public String getChargeboxes(Model model) {
        model.addAttribute("chargeboxes", chargeboxService.getAllChargeBox());
        model.addAttribute("seite", "chargeboxes");
        return "overviews/chargeboxOverview";
    }

    @GetMapping("/consumers")
    public String getConsumers(Model model) {
        model.addAttribute("consumers", consumerService.getAllConsumers());
        model.addAttribute("seite", "consumers");
        return "overviews/consumerOverview";
    }

    // Create

    @GetMapping("/anschlusse/new")
    public String addAnschluss(Model model) {
        anschlussService.addAnschluss(new Anschluss());
        model.addAttribute("anschlusse", anschlussService.getAll());
        model.addAttribute("seite", "anschlusse");
        return "redirect:/anschlusse";
    }

    @GetMapping("/chargeboxes/new")
    public String addChargebox() {
        ChargeBox chargeBox = new ChargeBox();
        chargeboxService.addChargeBox(chargeBox);
        anschlussService.addChargeboxToAnschluss(chargeBox);
        return "redirect:/chargeboxes";
    }

    @GetMapping("/consumers/new")
    public String addConsumer() {
        Consumer consumer = new Consumer();
        consumer.setName("Vebraucher" + new Random().nextInt(10000));
        consumerService.addConsumer(consumer);
        anschlussService.addConsumerToAnschluss(consumer);
        return "redirect:/consumers";
    }

    // Delete
    @GetMapping("/anschluss/delete/{id}")
    public String deleteAnschluss(@PathVariable long id) {
        // 1 darf nicht gelöscht werden
        if (id != 1) {
            anschlussService.deleteAnschluss(id);
        }
        return "redirect:/anschlusse";
    }

    @GetMapping("/chargebox/delete/{id}")
    public String deleteChargebox(@PathVariable long id) {
        chargeboxService.deleteChargeBox(id);
        return "redirect:/chargeboxes";
    }

    @GetMapping("/consumer/delete/{id}")
    public String deleteConsumer(@PathVariable long id) {
        consumerService.deleteConsumer(id);
        return "redirect:/consumers";
    }
}
