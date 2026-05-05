package com.campus.scems.service;

import com.campus.scems.model.EventType;
import com.campus.scems.repository.EventRepository;
import com.campus.scems.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public StatisticsService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public long totalActiveEvents() {
        return eventRepository.countByActiveTrue();
    }

    public long totalRegistrations() {
        return registrationRepository.countAllRegistrations();
    }

    public Map<String, Long> registrationsByDepartment() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : registrationRepository.registrationsByDepartment()) {
            map.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return map;
    }

    public Map<EventType, Long> registrationsByType() {
        Map<EventType, Long> map = new LinkedHashMap<>();
        for (Object[] row : registrationRepository.registrationsByEventType()) {
            map.put((EventType) row[0], ((Number) row[1]).longValue());
        }
        return map;
    }

    public List<Object[]> registrationCountsPerEvent() {
        return eventRepository.registrationCountsPerEvent();
    }
}
