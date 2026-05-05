package com.campus.event.config;

import com.campus.event.entity.Event;
import com.campus.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;

    @Override
    public void run(String... args) {
        if (eventRepository.count() == 0) {
            seedEvents();
            log.info("Sample events seeded successfully");
        }
    }

    private void seedEvents() {
        // Event 1
        Event e1 = new Event();
        e1.setTitle("National AI & Machine Learning Summit");
        e1.setDescription("Join industry leaders and researchers to explore the future of Artificial Intelligence and Machine Learning. Topics include deep learning, NLP, computer vision, and real-world AI deployments. Includes live demos and Q&A panels.");
        e1.setDate(LocalDateTime.now().plusDays(7));
        e1.setDepartment("Computer Science");
        e1.setType("Workshop");
        e1.setVenue("Main Auditorium, Block A");
        e1.setCapacity(200);
        e1.setStatus("UPCOMING");
        eventRepository.save(e1);

        // Event 2
        Event e2 = new Event();
        e2.setTitle("Cybersecurity Awareness & Ethical Hacking Bootcamp");
        e2.setDescription("A hands-on cybersecurity bootcamp covering penetration testing, ethical hacking techniques, network security fundamentals, and how to protect against modern threats. Suitable for beginners and intermediate students.");
        e2.setDate(LocalDateTime.now().plusDays(12));
        e2.setDepartment("Information Technology");
        e2.setType("Workshop");
        e2.setVenue("IT Lab, Block B");
        e2.setCapacity(80);
        e2.setStatus("UPCOMING");
        eventRepository.save(e2);

        // Event 3
        Event e3 = new Event();
        e3.setTitle("Entrepreneurship & Startup Innovation Seminar");
        e3.setDescription("Discover how to turn your ideas into a successful startup. Learn about business model canvas, funding strategies, lean startup methodology, and hear from successful founders about their journeys.");
        e3.setDate(LocalDateTime.now().plusDays(5));
        e3.setDepartment("Management");
        e3.setType("Seminar");
        e3.setVenue("Seminar Hall, Block C");
        e3.setCapacity(150);
        e3.setStatus("UPCOMING");
        eventRepository.save(e3);

        // Event 4
        Event e4 = new Event();
        e4.setTitle("Robotics & Embedded Systems Expo");
        e4.setDescription("An exciting expo where students can showcase their robotics and embedded systems projects. Includes live demonstrations, competitions, and a talk by renowned robotics engineers from industry.");
        e4.setDate(LocalDateTime.now().plusDays(20));
        e4.setDepartment("Electronics");
        e4.setType("Conference");
        e4.setVenue("Engineering Block, Ground Floor");
        e4.setCapacity(300);
        e4.setStatus("UPCOMING");
        eventRepository.save(e4);

        // Event 5
        Event e5 = new Event();
        e5.setTitle("Data Science & Analytics Masterclass");
        e5.setDescription("An intensive masterclass on data science workflows — from data collection, cleaning, EDA, visualization to model building and deployment. Participants will work with real datasets using Python and SQL.");
        e5.setDate(LocalDateTime.now().plusDays(3));
        e5.setDepartment("Computer Science");
        e5.setType("Workshop");
        e5.setVenue("Computer Lab 1, Block A");
        e5.setCapacity(60);
        e5.setStatus("UPCOMING");
        eventRepository.save(e5);

        // Event 6
        Event e6 = new Event();
        e6.setTitle("Annual Cultural Fest – TechnoFiesta 2025");
        e6.setDescription("The biggest cultural celebration of the year! Featuring music, dance, drama, hackathon, gaming tournaments, food stalls, and much more. Don't miss the closing ceremony with live performances.");
        e6.setDate(LocalDateTime.now().plusDays(30));
        e6.setDepartment("All Departments");
        e6.setType("Cultural");
        e6.setVenue("University Ground & Campus Auditorium");
        e6.setCapacity(1000);
        e6.setStatus("UPCOMING");
        eventRepository.save(e6);

        // Event 7
        Event e7 = new Event();
        e7.setTitle("Green Energy & Sustainability Conference");
        e7.setDescription("A multidisciplinary conference focusing on renewable energy solutions, sustainable engineering practices, climate change, and green innovation. Panel discussions and keynote speeches by environmental experts.");
        e7.setDate(LocalDateTime.now().plusDays(15));
        e7.setDepartment("Civil Engineering");
        e7.setType("Conference");
        e7.setVenue("Conference Hall, Admin Block");
        e7.setCapacity(120);
        e7.setStatus("UPCOMING");
        eventRepository.save(e7);

        // Event 8
        Event e8 = new Event();
        e8.setTitle("Full Stack Web Development Workshop");
        e8.setDescription("A comprehensive workshop covering HTML, CSS, JavaScript, React, Node.js, REST APIs, and database integration. Build a complete web application from scratch by the end of the workshop.");
        e8.setDate(LocalDateTime.now().plusDays(10));
        e8.setDepartment("Information Technology");
        e8.setType("Workshop");
        e8.setVenue("IT Lab 2, Block B");
        e8.setCapacity(50);
        e8.setStatus("UPCOMING");
        eventRepository.save(e8);
    }
}
