package com.campus.event.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
