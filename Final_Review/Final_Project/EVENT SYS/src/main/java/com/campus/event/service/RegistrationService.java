package com.campus.event.service;

import com.campus.event.dto.RegistrationRequest;
import com.campus.event.entity.Event;
import com.campus.event.entity.Registration;
import com.campus.event.entity.Student;
import com.campus.event.repository.EventRepository;
import com.campus.event.repository.RegistrationRepository;
import com.campus.event.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final EventRepository eventRepository;
    private final OtpService otpService;

    /**
     * Complete registration after OTP verification
     */
    public Registration registerForEvent(RegistrationRequest request) {
        // Verify OTP first
        if (!otpService.verifyOtp(request.getEmail(), request.getOtpCode())) {
            throw new RuntimeException("Invalid or expired OTP. Please try again.");
        }

        // Find or create student
        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    Student newStudent = new Student();
                    newStudent.setName(request.getName());
                    newStudent.setEmail(request.getEmail());
                    newStudent.setDepartment(request.getDepartment());
                    return studentRepository.save(newStudent);
                });

        // Update student details if changed
        student.setName(request.getName());
        student.setDepartment(request.getDepartment());
        studentRepository.save(student);

        // Get event
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if already registered
        if (registrationRepository.existsByStudentIdAndEventId(student.getId(), event.getId())) {
            throw new RuntimeException("You are already registered for this event.");
        }

        // Check event capacity
        if (!event.isRegistrationOpen()) {
            throw new RuntimeException("Event is fully booked or not accepting registrations.");
        }

        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setEvent(event);
        registration.setOtpVerified(true);

        Registration saved = registrationRepository.save(registration);
        log.info("Student {} registered for event {} successfully", student.getEmail(), event.getTitle());
        return saved;
    }

    /**
     * Get all registrations for a student by email
     */
    public List<Registration> getRegistrationsByEmail(String email) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty()) {
            return List.of();
        }
        return registrationRepository.findVerifiedByStudentId(studentOpt.get().getId());
    }

    /**
     * Get registrations for an event
     */
    public List<Registration> getRegistrationsByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public long getTotalRegistrationsCount() {
        return registrationRepository.countTotalRegistrations();
    }

    public List<Object[]> getRegistrationsByEvent() {
        return registrationRepository.countRegistrationsByEvent();
    }
}
