package com.campus.event.controller;

import com.campus.event.entity.Event;
import com.campus.event.service.EventService;
import com.campus.event.service.FeedbackService;
import com.campus.event.service.RegistrationService;
import com.campus.event.dto.StatsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final FeedbackService feedbackService;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        StatsResponse stats = new StatsResponse(
                eventService.getTotalEventsCount(),
                eventService.getUpcomingEventsCount(),
                registrationService.getTotalRegistrationsCount(),
                0L,
                feedbackService.getTotalFeedbackCount(),
                feedbackService.getOverallAverageRating()
        );
        model.addAttribute("stats", stats);
        model.addAttribute("recentEvents", eventService.getUpcomingEvents());
        return "admin/dashboard";
    }

    @GetMapping("/events")
    public String listEvents(Model model,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String type,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Event> events = eventService.searchEvents(department, type, status, startDate, endDate);
        model.addAttribute("events", events);
        model.addAttribute("filterDepartment", department);
        model.addAttribute("filterType", type);
        model.addAttribute("filterStatus", status);
        return "admin/events";
    }

    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("isEdit", false);
        return "admin/event-form";
    }

    @PostMapping("/events/new")
    public String createEvent(@Valid @ModelAttribute Event event,
                              BindingResult result,
                              RedirectAttributes redirectAttrs,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/event-form";
        }
        eventService.createEvent(event);
        redirectAttrs.addFlashAttribute("successMessage", "Event created successfully!");
        return "redirect:/admin/events";
    }

    @GetMapping("/events/{id}/edit")
    public String editEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("isEdit", true);
        return "admin/event-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute Event event,
                              BindingResult result,
                              RedirectAttributes redirectAttrs,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/event-form";
        }
        eventService.updateEvent(id, event);
        redirectAttrs.addFlashAttribute("successMessage", "Event updated successfully!");
        return "redirect:/admin/events";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        eventService.deleteEvent(id);
        redirectAttrs.addFlashAttribute("successMessage", "Event deleted successfully!");
        return "redirect:/admin/events";
    }

    @GetMapping("/events/{id}/registrations")
    public String viewRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("registrations", registrationService.getRegistrationsByEventId(id));
        return "admin/registrations";
    }

    @GetMapping("/stats")
    public String statsPage(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("totalRegistrations", registrationService.getTotalRegistrationsCount());
        model.addAttribute("overallRating", feedbackService.getOverallAverageRating());
        return "admin/stats";
    }
}
