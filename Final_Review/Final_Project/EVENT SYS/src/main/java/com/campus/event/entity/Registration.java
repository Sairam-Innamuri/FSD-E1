package com.campus.event.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "registrations"})
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "registrations", "feedbacks"})
    private Event event;

    @Column(nullable = false)
    private boolean otpVerified = false;

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(nullable = false)
    private String status = "CONFIRMED"; // CONFIRMED, CANCELLED, ATTENDED
}
