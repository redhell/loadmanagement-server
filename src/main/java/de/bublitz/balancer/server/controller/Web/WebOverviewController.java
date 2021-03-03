package de.bublitz.balancer.server.controller.Web;

import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
