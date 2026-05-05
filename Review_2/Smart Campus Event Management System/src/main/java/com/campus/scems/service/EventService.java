package com.campus.scems.service;

import com.campus.scems.exception.ResourceNotFoundException;
import com.campus.scems.model.EventEntity;
import com.campus.scems.model.EventType;
import com.campus.scems.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventEntity> upcoming(LocalDateTime from, String department, EventType type) {
        LocalDateTime effectiveFrom = from != null ? from : LocalDateTime.now();
        String dept = (department == null || department.isBlank()) ? null : department.trim();
        return eventRepository.findUpcomingFiltered(effectiveFrom, dept, type);
    }

    public List<EventEntity> upcomingDefault() {
        return eventRepository.findByActiveTrueAndStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime.now());
    }

    public EventEntity getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public EventEntity requireActiveUpcoming(Long id) {
        EventEntity e = getById(id);
        if (!e.isActive() || !e.getStartDateTime().isAfter(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Event not available for registration");
        }
        return e;
    }

    @Transactional
    public EventEntity save(EventEntity event) {
        return eventRepository.save(event);
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    public List<EventEntity> allForAdmin() {
        return eventRepository.findAll();
    }

    public long registrationCount(Long eventId) {
        return eventRepository.countRegistrationsByEventId(eventId);
    }
}
