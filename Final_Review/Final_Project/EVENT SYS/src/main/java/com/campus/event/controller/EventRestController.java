package com.campus.event.controller;

import com.campus.event.dto.ApiResponse;
import com.campus.event.dto.OtpRequest;
import com.campus.event.dto.RegistrationRequest;
import com.campus.event.dto.FeedbackRequest;
import com.campus.event.entity.Event;
import com.campus.event.entity.Feedback;
import com.campus.event.entity.Registration;
import com.campus.event.service.EventService;
import com.campus.event.service.FeedbackService;
import com.campus.event.service.OtpService;
import com.campus.event.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class EventRestController {

    private final EventService eventService;
    private final OtpService otpService;
    private final RegistrationService registrationService;
    private final FeedbackService feedbackService;

    // ========== EVENT APIs ==========

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<Event>>> getAllEvents() {
        return ResponseEntity.ok(ApiResponse.success("Events fetched", eventService.getUpcomingEvents()));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<ApiResponse<Event>> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(e -> ResponseEntity.ok(ApiResponse.success("Event found", e)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== OTP APIs ==========

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody OtpRequest request) {
        try {
            otpService.generateAndSendOtp(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(
                    "OTP sent to " + request.getEmail() + ". Please check your inbox."
            ));
        } catch (Exception e) {
            log.error("Error sending OTP: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to send OTP. Please try again."));
        }
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otpCode = body.get("otpCode");

        if (email == null || otpCode == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email and OTP code are required"));
        }

        boolean verified = otpService.verifyOtp(email, otpCode);
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("OTP verified successfully!", true));
        } else {
            return ResponseEntity.ok(ApiResponse.error("Invalid or expired OTP"));
        }
    }

    // ========== REGISTRATION APIs ==========

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Registration>> register(@Valid @RequestBody RegistrationRequest request) {
        try {
            Registration registration = registrationService.registerForEvent(request);
            return ResponseEntity.ok(ApiResponse.success(
                    "Registration successful! You are registered for the event.", registration
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-events")
    public ResponseEntity<ApiResponse<List<Registration>>> getMyEvents(@RequestParam String email) {
        List<Registration> registrations = registrationService.getRegistrationsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Your events fetched", registrations));
    }

    // ========== FEEDBACK APIs ==========

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<Feedback>> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        try {
            Feedback feedback = feedbackService.submitFeedback(request);
            return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully!", feedback));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/feedback/{eventId}")
    public ResponseEntity<ApiResponse<List<Feedback>>> getEventFeedback(@PathVariable Long eventId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Feedback fetched", feedbacks));
    }

    // ========== ADMIN APIs ==========

    @GetMapping("/admin/events/search")
    public ResponseEntity<ApiResponse<List<Event>>> searchEvents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        List<Event> events;
        if (keyword != null && !keyword.isBlank()) {
            events = eventService.searchByKeyword(keyword);
        } else {
            events = eventService.searchEvents(department, type, status, null, null);
        }
        return ResponseEntity.ok(ApiResponse.success("Search results", events));
    }
}
