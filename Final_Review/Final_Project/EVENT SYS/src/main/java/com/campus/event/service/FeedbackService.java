package com.campus.event.service;

import com.campus.event.dto.FeedbackRequest;
import com.campus.event.entity.Event;
import com.campus.event.entity.Feedback;
import com.campus.event.entity.Student;
import com.campus.event.repository.EventRepository;
import com.campus.event.repository.FeedbackRepository;
import com.campus.event.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository;
    private final EventRepository eventRepository;

    public Feedback submitFeedback(FeedbackRequest request) {
        if (feedbackRepository.existsByStudentIdAndEventId(request.getStudentId(), request.getEventId())) {
            throw new RuntimeException("You have already submitted feedback for this event.");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Feedback feedback = new Feedback();
        feedback.setStudent(student);
        feedback.setEvent(event);
        feedback.setComments(request.getComments());
        feedback.setRating(request.getRating());

        Feedback saved = feedbackRepository.save(feedback);
        log.info("Feedback submitted by {} for event {}", student.getEmail(), event.getTitle());
        return saved;
    }

    public List<Feedback> getFeedbackByEvent(Long eventId) {
        return feedbackRepository.findByEventId(eventId);
    }

    public Double getAverageRating(Long eventId) {
        return feedbackRepository.getAverageRatingByEventId(eventId);
    }

    public long getTotalFeedbackCount() {
        return feedbackRepository.count();
    }

    public double getOverallAverageRating() {
        List<Feedback> all = feedbackRepository.findAll();
        if (all.isEmpty()) return 0.0;
        return all.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
    }
}
