package com.campus.scems.config;

import com.campus.scems.model.AppUser;
import com.campus.scems.model.EventEntity;
import com.campus.scems.model.EventType;
import com.campus.scems.model.RegistrationEntity;
import com.campus.scems.model.UserRole;
import com.campus.scems.repository.AppUserRepository;
import com.campus.scems.repository.EventRepository;
import com.campus.scems.repository.RegistrationRepository;
import com.campus.scems.util.VeltechLoginIds;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final AppUserRepository appUserRepository;
    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            EventRepository eventRepository,
            AppUserRepository appUserRepository,
            RegistrationRepository registrationRepository,
            PasswordEncoder passwordEncoder) {
        this.eventRepository = eventRepository;
        this.appUserRepository = appUserRepository;
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedEvents();
        seedRegistrations();
    }

    private void seedUsers() {
        String facPass = "faculty123";
        for (String id : VeltechLoginIds.FACULTY_SEED_IDS) {
            if (appUserRepository.existsByLoginIdIgnoreCase(id)) {
                continue;
            }
            AppUser f = new AppUser();
            f.setLoginId(id);
            f.setFullName("Faculty " + id);
            f.setEmail(id.toLowerCase() + "@veltech.edu");
            f.setPasswordHash(passwordEncoder.encode(facPass));
            f.setRole(UserRole.FACULTY);
            f.setEnabled(true);
            appUserRepository.save(f);
        }
        if (!appUserRepository.existsByLoginIdIgnoreCase("VTU10001")) {
            AppUser student = new AppUser();
            student.setLoginId("VTU10001");
            student.setFullName("Demo Student");
            student.setEmail("vtu10001@veltech.edu");
            student.setPasswordHash(passwordEncoder.encode("student123"));
            student.setRole(UserRole.STUDENT);
            student.setEnabled(true);
            appUserRepository.save(student);
        }

        // Backfill legacy databases (older schema used EMAIL as the only identifier).
        appUserRepository.findAll().forEach(u -> {
            boolean changed = false;

            String email = u.getEmail() == null ? "" : u.getEmail().trim();
            String loginId = u.getLoginId() == null ? "" : u.getLoginId().trim();

            if (loginId.isBlank()) {
                if (u.getRole() == UserRole.FACULTY && "faculty@campus.edu".equalsIgnoreCase(email)) {
                    u.setLoginId("VTUF999");
                    changed = true;
                } else if (u.getRole() == UserRole.STUDENT && "student@campus.edu".equalsIgnoreCase(email)) {
                    u.setLoginId("VTU99999");
                    changed = true;
                } else if (!email.isBlank()) {
                    u.setLoginId(email.toUpperCase());
                    changed = true;
                }
            }

            if (email.isBlank()) {
                String id = u.getLoginId() == null ? ("user" + u.getId()) : u.getLoginId().trim().toLowerCase();
                u.setEmail(id + "@veltech.edu");
                changed = true;
            }

            if (changed) {
                appUserRepository.save(u);
            }
        });
    }

    private void seedEvents() {
        if (eventRepository.count() > 0) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        eventRepository.save(sample(
                "AI & Campus Innovation Workshop",
                "Hands-on session on building small ML demos and responsible AI practices for campus projects.",
                now.plusDays(3).withHour(10).withMinute(0),
                now.plusDays(3).withHour(13).withMinute(0),
                EventType.WORKSHOP,
                "Computer Science",
                "Innovation Lab — Block A",
                80));
        eventRepository.save(sample(
                "Industry Seminar: Cloud Careers",
                "A seminar covering certifications, internships, and interview patterns for cloud roles.",
                now.plusDays(7).withHour(15).withMinute(30),
                now.plusDays(7).withHour(17).withMinute(0),
                EventType.SEMINAR,
                "Information Technology",
                "Auditorium — Main Campus",
                200));
        eventRepository.save(sample(
                "48-Hour Campus Hackathon",
                "Build solutions for smart attendance, energy usage, and event discovery.",
                now.plusDays(14).withHour(9).withMinute(0),
                now.plusDays(16).withHour(18).withMinute(0),
                EventType.HACKATHON,
                "Computer Science",
                "Tech Hub — Basement",
                120));
        eventRepository.save(sample(
                "Spring Fest — Open Mic Night",
                "Celebrate student talent with music, poetry, and stand-up performances.",
                now.plusDays(10).withHour(18).withMinute(0),
                now.plusDays(10).withHour(21).withMinute(0),
                EventType.CULTURAL,
                "Student Affairs",
                "Open Amphitheatre",
                300));
        eventRepository.save(sample(
                "Placement Drive — Core Engineering",
                "On-campus briefing and mock interviews for core engineering roles.",
                now.plusDays(5).withHour(11).withMinute(0),
                now.plusDays(5).withHour(14).withMinute(0),
                EventType.PLACEMENT,
                "Mechanical Engineering",
                "Seminar Hall — Block C",
                60));
    }

    private void seedRegistrations() {
        // Demo data: ensure each seeded event has 8 registrations.
        // Uses the demo student login ID (stored in RegistrationEntity.studentEmail for now).
        String demoStudentId = "VTU10001";
        for (EventEntity e : eventRepository.findAll()) {
            long existing = eventRepository.countRegistrationsByEventId(e.getId());
            if (existing >= 8) {
                continue;
            }
            int toCreate = (int) Math.min(8 - existing, Math.max(0, e.getCapacity() - (int) existing));
            for (int i = 1; i <= toCreate; i++) {
                RegistrationEntity r = new RegistrationEntity();
                r.setEvent(e);
                r.setStudentName("Demo Student " + i);
                r.setStudentEmail(demoStudentId);
                r.setStudentRoll("DEMO-" + e.getId() + "-" + String.format("%02d", i));
                r.setRegisteredAt(LocalDateTime.now().minusDays(1).plusMinutes(i));
                if (!registrationRepository.existsByEventIdAndStudentRollIgnoreCase(e.getId(), r.getStudentRoll())) {
                    registrationRepository.save(r);
                }
            }
        }
    }

    private static EventEntity sample(
            String title,
            String description,
            LocalDateTime start,
            LocalDateTime end,
            EventType type,
            String department,
            String venue,
            int capacity) {
        EventEntity e = new EventEntity();
        e.setTitle(title);
        e.setDescription(description);
        e.setStartDateTime(start);
        e.setEndDateTime(end);
        e.setEventType(type);
        e.setDepartment(department);
        e.setVenue(venue);
        e.setCapacity(capacity);
        e.setActive(true);
        return e;
    }
}
