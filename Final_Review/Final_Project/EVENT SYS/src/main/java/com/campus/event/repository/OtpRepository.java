package com.campus.event.repository;

import com.campus.event.entity.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpRecord, Long> {

    Optional<OtpRecord> findTopByEmailOrderByCreatedAtDesc(String email);

    @Query("SELECT o FROM OtpRecord o WHERE o.email = :email AND o.verified = false AND o.expiryTime > :now ORDER BY o.createdAt DESC")
    Optional<OtpRecord> findValidOtp(@Param("email") String email, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpRecord o WHERE o.expiryTime < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);
}
