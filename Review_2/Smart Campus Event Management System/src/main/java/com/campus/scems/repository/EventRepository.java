package com.campus.scems.repository;

import com.campus.scems.model.EventEntity;
import com.campus.scems.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findByActiveTrueAndStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime now);

    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.active = true
              AND e.startDateTime >= :from
              AND (:department IS NULL OR :department = '' OR LOWER(e.department) = LOWER(:department))
              AND (:type IS NULL OR e.eventType = :type)
            ORDER BY e.startDateTime ASC
            """)
    List<EventEntity> findUpcomingFiltered(
            @Param("from") LocalDateTime from,
            @Param("department") String department,
            @Param("type") EventType type);

    long countByActiveTrue();

    @Query("SELECT COUNT(r) FROM RegistrationEntity r WHERE r.event.id = :eventId")
    long countRegistrationsByEventId(@Param("eventId") Long eventId);

    @Query("""
            SELECT e.title, COUNT(r)
            FROM EventEntity e
            LEFT JOIN e.registrations r
            WHERE e.active = true
            GROUP BY e.id, e.title
            ORDER BY COUNT(r) DESC
            """)
    List<Object[]> registrationCountsPerEvent();

    @Query("SELECT DISTINCT e.department FROM EventEntity e WHERE e.active = true ORDER BY e.department")
    List<String> findDistinctActiveDepartments();
}
