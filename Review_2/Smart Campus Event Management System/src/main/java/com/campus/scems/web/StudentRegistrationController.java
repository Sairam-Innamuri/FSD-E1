package com.campus.scems.web;

import com.campus.scems.model.EventEntity;
import com.campus.scems.model.RegistrationEntity;
import com.campus.scems.service.EventService;
import com.campus.scems.service.RegistrationService;
import com.campus.scems.web.dto.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentRegistrationController {

    private final EventService eventService;
    private final RegistrationService registrationService;

    public StudentRegistrationController(EventService eventService, RegistrationService registrationService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping("/events/{id}/register")
    public String registerForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        EventEntity event = eventService.requireActiveUpcoming(id);
        model.addAttribute("event", event);
        model.addAttribute("studentEmail", user.getUsername());
        model.addAttribute("form", new RegistrationForm());
        return "student/register";
    }

    @PostMapping("/events/{id}/register")
    public String registerSubmit(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") RegistrationForm form,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        EventEntity event = eventService.requireActiveUpcoming(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", event);
            model.addAttribute("studentEmail", user.getUsername());
            return "student/register";
        }
        RegistrationEntity reg = new RegistrationEntity();
        reg.setStudentName(form.getStudentName().trim());
        reg.setStudentEmail(user.getUsername());
        reg.setStudentRoll(form.getStudentRoll().trim());
        registrationService.register(id, reg);
        redirectAttributes.addFlashAttribute("successMessage", "You are registered! You can review it under My registrations.");
        return "redirect:/student/registrations";
    }

    @GetMapping("/registrations")
    public String myRegistrations(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("email", user.getUsername());
        model.addAttribute("registrations", registrationService.findByStudentEmail(user.getUsername()));
        return "student/my-registrations";
    }
}
