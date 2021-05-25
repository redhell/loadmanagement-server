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

    @GetMapping("/chargebox/edit/{id}")
    public String getChargebox(Model model, @PathVariable long id) {
        model.addAttribute("chargebox", chargeboxService.getChargeboxById(id));
        model.addAttribute("voltages", new int[]{230, 400});
        model.addAttribute("anschlusse", anschlussService.getAll());
        return "edits/chargebox";
    }

    @GetMapping("/consumer/edit/{id}")
    public String getConsumer(Model model, @PathVariable long id) {
        model.addAttribute("consumer", consumerService.getConsumerById(id));
        model.addAttribute("anschlusse", anschlussService.getAll());
        return "edits/consumer";
    }

    // Save settings
    @PostMapping("/anschluss/edit/{id}")
    public ModelAndView postAnschluss(@ModelAttribute Anschluss anschluss, @PathVariable long id) {
        ModelAndView mav = new ModelAndView("redirect:/anschluss/" + id);
        anschlussService.update(anschluss);
        return mav;
    }

    @PostMapping("/chargebox/edit/{id}")
    public ModelAndView postChargebox(@ModelAttribute ChargeBox chargeBox, @PathVariable long id) {
        ModelAndView mav = new ModelAndView("redirect:/chargebox/" + id);
        chargeboxService.update(chargeBox);
        return mav;
    }

    @PostMapping("/consumer/edit/{id}")
    public ModelAndView postConsumer(@ModelAttribute Consumer consumer, @PathVariable long id) {
        ModelAndView mav = new ModelAndView("redirect:/consumer/" + id);
        consumer.setConsumerID(id);
        consumerService.update(consumer);
        return mav;
    }
}
