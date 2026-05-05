package com.campus.scems.web.admin;

import com.campus.scems.model.EventEntity;
import com.campus.scems.model.EventType;
import com.campus.scems.service.EventService;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
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
@RequestMapping("/faculty/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("events", eventService.allForAdmin());
        return "faculty/events/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("event", blankEvent());
        model.addAttribute("types", EventType.values());
        return "faculty/events/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("event") EventEntity event,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("types", EventType.values());
            return "faculty/events/form";
        }
        event.setId(null);
        eventService.save(event);
        redirectAttributes.addFlashAttribute("successMessage", "Event created.");
        return "redirect:/faculty/events";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getById(id));
        model.addAttribute("types", EventType.values());
        return "faculty/events/form";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(
            @PathVariable Long id,
            @Valid @ModelAttribute("event") EventEntity incoming,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("types", EventType.values());
            return "faculty/events/form";
        }
        EventEntity existing = eventService.getById(id);
        existing.setTitle(incoming.getTitle());
        existing.setDescription(incoming.getDescription());
        existing.setStartDateTime(incoming.getStartDateTime());
        existing.setEndDateTime(incoming.getEndDateTime());
        existing.setEventType(incoming.getEventType());
        existing.setDepartment(incoming.getDepartment());
        existing.setVenue(incoming.getVenue());
        existing.setCapacity(incoming.getCapacity());
        existing.setActive(incoming.isActive());
        eventService.save(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Event updated.");
        return "redirect:/faculty/events";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted.");
        return "redirect:/faculty/events";
    }

    private static EventEntity blankEvent() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
        EventEntity e = new EventEntity();
        e.setActive(true);
        e.setCapacity(50);
        e.setEventType(EventType.OTHER);
        e.setDepartment("General");
        e.setVenue("TBD");
        e.setTitle("");
        e.setStartDateTime(start);
        e.setEndDateTime(start.plusHours(2));
        return e;
    }
}
