package com.campus.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsResponse {
    private long totalEvents;
    private long upcomingEvents;
    private long totalRegistrations;
    private long totalStudents;
    private long totalFeedbacks;
    private double averageRating;
}
