package com.campus.event.service;

import com.campus.event.entity.Event;
import com.campus.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        log.info("Creating new event: {}", event.getTitle());
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        existing.setTitle(updatedEvent.getTitle());
        existing.setDescription(updatedEvent.getDescription());
        existing.setDate(updatedEvent.getDate());
        existing.setDepartment(updatedEvent.getDepartment());
        existing.setType(updatedEvent.getType());
        existing.setVenue(updatedEvent.getVenue());
        existing.setCapacity(updatedEvent.getCapacity());
        existing.setStatus(updatedEvent.getStatus());
        log.info("Updated event with id: {}", id);
        return eventRepository.save(existing);
    }

    public void deleteEvent(Long id) {
        log.info("Deleting event with id: {}", id);
        eventRepository.deleteById(id);
    }

    public List<Event> searchEvents(String department, String type, String status,
                                    LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.searchEvents(department, type, status, startDate, endDate);
    }

    public List<Event> searchByKeyword(String keyword) {
        return eventRepository.searchByKeyword(keyword);
    }

    public List<Event> getEventsByDepartment(String department) {
        return eventRepository.findByDepartmentIgnoreCase(department);
    }

    public List<Event> getEventsByType(String type) {
        return eventRepository.findByTypeIgnoreCase(type);
    }

    public long getTotalEventsCount() {
        return eventRepository.count();
    }

    public long getUpcomingEventsCount() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now()).size();
    }
}
