package com.campus.scems.web.admin;

import com.campus.scems.service.EventService;
import com.campus.scems.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faculty/events")
public class AdminRegistrationController {

    private final EventService eventService;
    private final RegistrationService registrationService;

    public AdminRegistrationController(EventService eventService, RegistrationService registrationService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping("/{id}/registrations")
    public String registrations(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getById(id));
        model.addAttribute("registrations", registrationService.forEvent(id));
        return "faculty/event-registrations";
    }
}
