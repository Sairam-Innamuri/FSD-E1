package com.campus.scems.api;

import com.campus.scems.model.EventEntity;
import com.campus.scems.model.EventType;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        EventType eventType,
        String department,
        String venue,
        Integer capacity,
        long registeredCount,
        int spotsLeft,
        boolean active) {

    public static EventResponse from(EventEntity e, long registeredCount) {
        int left = Math.max(0, e.getCapacity() - (int) registeredCount);
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getStartDateTime(),
                e.getEndDateTime(),
                e.getEventType(),
                e.getDepartment(),
                e.getVenue(),
                e.getCapacity(),
                registeredCount,
                left,
                e.isActive());
    }
}
