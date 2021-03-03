package de.bublitz.balancer.server.controller.Web;

import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.model.enums.LoadStrategy;
import de.bublitz.balancer.server.service.AnschlussService;
import de.bublitz.balancer.server.service.ChargeboxService;
import de.bublitz.balancer.server.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebEditController {
    @Autowired
    private AnschlussService anschlussService;
    @Autowired
    private ChargeboxService chargeboxService;
    @Autowired
    private ConsumerService consumerService;

    // Get Editpage
    @GetMapping("/anschluss/edit/{id}")
    public String getAnschlussEdit(Model model, @PathVariable long id) {
        model.addAttribute("anschluss", anschlussService.getAnschlussById(id));
        model.addAttribute("strategies", LoadStrategy.values());
        return "edits/anschluss";
    }

    @GetMapping("/chargebox/edit/{evseid}")
    public String getChargebox(Model model, @PathVariable String evseid) {
        model.addAttribute("chargebox", chargeboxService.getChargeBoxByName(evseid));
        return "chargebox";
    }

    @GetMapping("/consumer/edit/{name}")
    public String getConsumer(Model model, @PathVariable String name) {
        model.addAttribute("consumer", consumerService.getConsumerByName(name));
        return "consumer";
    }

    // Save settings
    @PostMapping("/anschluss/edit/{id}")
    public ModelAndView postAnschluss(@ModelAttribute Anschluss anschluss, @PathVariable long id) {
        ModelAndView mav = new ModelAndView("redirect:/anschluss/" + id);
        anschlussService.update(anschluss);
        return mav;
    }

    @PostMapping("/chargebox/edit/{id}")
    public ModelAndView postChargebox(@ModelAttribute ChargeBox chargeBox, @PathVariable String evseid) {
        ModelAndView mav = new ModelAndView("redirect:/chargebox/" + evseid);
        chargeboxService.update(chargeBox);
        return mav;
    }

    @PostMapping("/consumer/edit/{id}")
    public ModelAndView postConsumer(@ModelAttribute Consumer consumer, @PathVariable String name) {
        ModelAndView mav = new ModelAndView("redirect:/consumer/" + name);
        consumerService.update(consumer);
        return mav;
    }
}
