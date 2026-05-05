package com.campus.event.repository;

import com.campus.event.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByStudentId(Long studentId);

    List<Registration> findByEventId(Long eventId);

    Optional<Registration> findByStudentIdAndEventId(Long studentId, Long eventId);

    boolean existsByStudentIdAndEventId(Long studentId, Long eventId);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.id = :eventId AND r.otpVerified = true")
    long countVerifiedByEventId(@Param("eventId") Long eventId);

    @Query("SELECT r FROM Registration r JOIN FETCH r.event JOIN FETCH r.student WHERE r.student.id = :studentId AND r.otpVerified = true")
    List<Registration> findVerifiedByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(r) FROM Registration r")
    long countTotalRegistrations();

    @Query("SELECT r.event.id, COUNT(r) FROM Registration r WHERE r.otpVerified = true GROUP BY r.event.id")
    List<Object[]> countRegistrationsByEvent();
}
