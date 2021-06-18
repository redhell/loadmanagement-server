package de.bublitz.balancer.server.controller.Web;

import de.bublitz.balancer.server.components.BalancerComponent;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.ConsumerService;
import de.bublitz.balancer.server.service.DynamicSchedulerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    @Autowired
    private BalancerComponent balancerComponent;
    @Autowired
    private DynamicSchedulerService dynamicSchedulerService;

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("anschlusse", anschlussService.getAll());
        model.addAttribute("chargeboxes", chargeboxService.getAllChargeBox());
        model.addAttribute("consumers", consumerService.getAllConsumers());
        model.addAttribute("seite", "index");
        return "index";
    }

    @PostMapping("/settings/update")
    public ModelAndView postSettings(@RequestParam int balanceRate) {
        ModelAndView modelAndView = new ModelAndView("redirect:/settings");
        dynamicSchedulerService.setBalancingRate(balanceRate);
        log.debug("Updating intervall Rate: " + balanceRate);
        return modelAndView;
    }

    @GetMapping("/settings")
    public String getSettings(Model model) {
        model.addAttribute("seite", "settings");
        model.addAttribute("balanceRate", dynamicSchedulerService.getBalancingRate());
        return "settings";
    }

    @GetMapping("/anschluss/{id}")
    public String getAnschluss(Model model, @PathVariable long id) {
        model.addAttribute("anschluss", anschlussService.getAnschlussById(id));
        return "anschluss";
    }

    @GetMapping("/anschluss/{id}/optimize")
    public String triggerOptimize(Model model, @PathVariable long id) {
        Anschluss anschluss = anschlussService.getAnschlussById(id);
        balancerComponent.triggerBalance(anschluss);
        model.addAttribute("anschluss", anschluss);
        model.addAttribute("optimized", true);
        return "anschluss";
    }

    @GetMapping("/chargebox/{id}")
    public String getChargebox(Model model, @PathVariable long id) {
        model.addAttribute("chargebox", chargeboxService.getChargeboxById(id));
        return "chargebox";
    }

    @GetMapping("/consumer/{id}")
    public String getConsumer(Model model, @PathVariable long id) {
        model.addAttribute("consumer", consumerService.getConsumerById(id));
        return "consumer";
    }

}
