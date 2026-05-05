package com.campus.scems.api;

import com.campus.scems.model.EventEntity;
import com.campus.scems.model.EventType;
import com.campus.scems.repository.EventRepository;
import com.campus.scems.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class ApiEventController {

    private final EventService eventService;
    private final EventRepository eventRepository;

    public ApiEventController(EventService eventService, EventRepository eventRepository) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/upcoming")
    public List<EventResponse> upcoming(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        LocalDateTime effectiveFrom = from != null ? from : LocalDateTime.now();
        List<EventEntity> list = eventService.upcoming(effectiveFrom, department, type);
        return list.stream()
                .map(e -> EventResponse.from(e, eventRepository.countRegistrationsByEventId(e.getId())))
                .toList();
    }

    @GetMapping("/departments")
    public List<String> departments() {
        return eventRepository.findDistinctActiveDepartments();
    }
}
