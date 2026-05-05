package com.campus.scems.web;

import com.campus.scems.model.EventEntity;
import com.campus.scems.model.EventType;
import com.campus.scems.repository.EventRepository;
import com.campus.scems.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/events")
public class StudentEventController {

    private final EventService eventService;
    private final EventRepository eventRepository;

    public StudentEventController(EventService eventService, EventRepository eventRepository) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public String list(
            Model model,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        LocalDateTime effectiveFrom = from != null ? from : LocalDateTime.now();
        List<EventEntity> events = eventService.upcoming(effectiveFrom, department, type);
        model.addAttribute("events", events);
        model.addAttribute("departments", eventRepository.findDistinctActiveDepartments());
        model.addAttribute("types", EventType.values());
        model.addAttribute("filterDepartment", department);
        model.addAttribute("filterType", type);
        model.addAttribute("filterFrom", from);
        model.addAttribute("counts", events.stream()
                .collect(java.util.stream.Collectors.toMap(EventEntity::getId, e -> eventRepository.countRegistrationsByEventId(e.getId()))));
        return "events/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        EventEntity event = eventService.getById(id);
        long count = eventRepository.countRegistrationsByEventId(id);
        model.addAttribute("event", event);
        model.addAttribute("registeredCount", count);
        model.addAttribute("spotsLeft", Math.max(0, event.getCapacity() - (int) count));
        return "events/detail";
    }
}
