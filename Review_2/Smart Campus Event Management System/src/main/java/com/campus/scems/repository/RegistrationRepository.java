package com.campus.scems.repository;

import com.campus.scems.model.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

    @Query("SELECT COUNT(r) FROM RegistrationEntity r")
    long countAllRegistrations();

    @Query("SELECT e.department, COUNT(r) FROM RegistrationEntity r JOIN r.event e GROUP BY e.department ORDER BY COUNT(r) DESC")
    List<Object[]> registrationsByDepartment();

    @Query("SELECT e.eventType, COUNT(r) FROM RegistrationEntity r JOIN r.event e GROUP BY e.eventType ORDER BY COUNT(r) DESC")
    List<Object[]> registrationsByEventType();

    List<RegistrationEntity> findByStudentEmailIgnoreCaseOrderByRegisteredAtDesc(String email);

    boolean existsByEventIdAndStudentRollIgnoreCase(Long eventId, String studentRoll);

    @Query("SELECT r FROM RegistrationEntity r JOIN FETCH r.event e WHERE r.id = :id")
    Optional<RegistrationEntity> findByIdWithEvent(@Param("id") Long id);

    List<RegistrationEntity> findByEventIdOrderByRegisteredAtAsc(Long eventId);

    @Query("""
            SELECT r
            FROM RegistrationEntity r
            JOIN FETCH r.event e
            LEFT JOIN FETCH r.feedback f
            WHERE e.id = :eventId
            ORDER BY r.registeredAt ASC
            """)
    List<RegistrationEntity> findByEventIdWithEventAndFeedbackOrderByRegisteredAtAsc(@Param("eventId") Long eventId);

    @Query("SELECT r FROM RegistrationEntity r JOIN FETCH r.event ORDER BY r.registeredAt DESC")
    List<RegistrationEntity> findAllWithEvent();
}
