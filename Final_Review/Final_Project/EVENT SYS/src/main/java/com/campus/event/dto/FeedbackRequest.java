package com.campus.event.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "Comments are required")
    @Size(min = 10, max = 1000, message = "Comments must be between 10 and 1000 characters")
    private String comments;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating;
}
