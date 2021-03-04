package de.bublitz.balancer.server.controller.Web;

import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.ConsumerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/anschluss/{id}")
    public String getAnschluss(Model model, @PathVariable long id) {
        model.addAttribute("anschluss", anschlussService.getAnschlussById(id));
        return "anschluss";
    }

    @GetMapping("/chargebox/{evseid}")
    public String getChargebox(Model model, @PathVariable String evseid) {
        model.addAttribute("chargebox", chargeboxService.getChargeBoxByName(evseid));
        return "chargebox";
    }

    @GetMapping("/consumer/{id}")
    public String getConsumer(Model model, @PathVariable long id) {
        model.addAttribute("consumer", consumerService.getConsumerById(id));
        return "consumer";
    }

}
