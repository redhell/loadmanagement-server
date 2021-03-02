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
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView getIndex() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("anschlusse", anschlussService.getAll());
        mav.addObject("chargeboxes", chargeboxService.getAllChargeBox());
        mav.addObject("consumers", consumerService.getAllConsumers());

        return mav;
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
        return "overviews/anschlussOverview";
    }

    @GetMapping("/chargeboxes")
    public String getChargeboxes(Model model) {
        model.addAttribute("chargeboxes", chargeboxService.getAllChargeBox());
        return "overviews/chargeboxOverview";
    }

    @GetMapping("/consumers")
    public String getConsumers(Model model) {
        model.addAttribute("consumers", consumerService.getAllConsumers());
        return "overviews/consumerOverview";
    }
}
