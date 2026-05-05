package com.campus.event.controller;

import com.campus.event.entity.Event;
import com.campus.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EventService eventService;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String department,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) String keyword) {

        List<Event> events;

        if (keyword != null && !keyword.isBlank()) {
            events = eventService.searchByKeyword(keyword);
            model.addAttribute("keyword", keyword);
        } else if (department != null && !department.isBlank()) {
            events = eventService.getEventsByDepartment(department);
            model.addAttribute("selectedDepartment", department);
        } else if (type != null && !type.isBlank()) {
            events = eventService.getEventsByType(type);
            model.addAttribute("selectedType", type);
        } else {
            events = eventService.getUpcomingEvents();
        }

        model.addAttribute("events", events);
        model.addAttribute("totalEvents", eventService.getTotalEventsCount());
        model.addAttribute("upcomingEvents", eventService.getUpcomingEventsCount());
        return "index";
    }

    @GetMapping("/events")
    public String allEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "events";
    }

    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        return "event-detail";
    }

    @GetMapping("/my-events")
    public String myEvents(Model model) {
        model.addAttribute("pageTitle", "My Registered Events");
        return "my-events";
    }
}
