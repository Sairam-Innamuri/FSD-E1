package com.campus.scems.service;

import com.campus.scems.exception.BusinessRuleException;
import com.campus.scems.exception.ResourceNotFoundException;
import com.campus.scems.model.EventEntity;
import com.campus.scems.model.RegistrationEntity;
import com.campus.scems.repository.EventRepository;
import com.campus.scems.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public RegistrationService(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            EventService eventService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    @Transactional
    public RegistrationEntity register(Long eventId, RegistrationEntity form) {
        EventEntity event = eventService.requireActiveUpcoming(eventId);
        long count = eventRepository.countRegistrationsByEventId(eventId);
        if (count >= event.getCapacity()) {
            throw new BusinessRuleException("This event is full.");
        }
        if (registrationRepository.existsByEventIdAndStudentRollIgnoreCase(eventId, form.getStudentRoll())) {
            throw new BusinessRuleException("This roll number is already registered for this event.");
        }
        form.setEvent(event);
        form.setRegisteredAt(LocalDateTime.now());
        return registrationRepository.save(form);
    }

    public List<RegistrationEntity> findByStudentEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }
        return registrationRepository.findByStudentEmailIgnoreCaseOrderByRegisteredAtDesc(email.trim());
    }

    public RegistrationEntity getByIdWithEvent(Long id) {
        return registrationRepository.findByIdWithEvent(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
    }

    public List<RegistrationEntity> forEvent(Long eventId) {
        eventService.getById(eventId);
        return registrationRepository.findByEventIdWithEventAndFeedbackOrderByRegisteredAtAsc(eventId);
    }
}
