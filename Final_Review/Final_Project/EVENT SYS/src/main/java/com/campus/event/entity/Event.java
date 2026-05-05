package com.campus.event.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Event date is required")
    @Column(nullable = false)
    private LocalDateTime date;

    @NotBlank(message = "Department is required")
    @Column(nullable = false)
    private String department;

    @NotBlank(message = "Event type is required")
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private int capacity = 100;

    @Column(nullable = false)
    private String status = "UPCOMING"; // UPCOMING, ONGOING, COMPLETED, CANCELLED

    @Column(nullable = false)
    private String imageUrl = "/images/default-event.jpg";

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    public int getRegistrationCount() {
        if (registrations == null) return 0;
        try { return registrations.size(); } catch (Exception e) { return 0; }
    }

    public boolean isRegistrationOpen() {
        return getRegistrationCount() < capacity && "UPCOMING".equals(status);
    }
}
