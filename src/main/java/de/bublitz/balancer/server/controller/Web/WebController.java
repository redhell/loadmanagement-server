package de.bublitz.balancer.server.controller.Web;

import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.ConsumerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
public class WebController {
    @Autowired
    private AnschlussService anschlussService;
    @Autowired
    private ChargeboxService chargeboxService;
    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("anschlusse", anschlussService.getAll());
        model.addAttribute("chargeboxes", chargeboxService.getAllChargeBox());
        model.addAttribute("consumers", consumerService.getAllConsumers());
        model.addAttribute("seite", "index");
        return "index";
    }

    @GetMapping("/anschluss")
    public String getAnschluss(Model model, @RequestParam long id) {
        model.addAttribute("anschluss", anschlussService.getAnschlussById(id));
        return "anschluss";
    }

    @GetMapping("/chargebox")
    public String getChargebox(Model model, @RequestParam String evseid) {
        model.addAttribute("chargebox", chargeboxService.getChargeBoxByName(evseid));
        return "chargebox";
    }

    @GetMapping("/consumer")
    public String getConsumer(Model model, @RequestParam String name) {
        model.addAttribute("consumer", consumerService.getConsumerByName(name));
        return "consumer";
    }

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
}
