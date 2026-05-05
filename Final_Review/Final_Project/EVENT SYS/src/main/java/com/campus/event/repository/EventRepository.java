package com.campus.event.repository;

import com.campus.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByDepartmentIgnoreCase(String department);

    List<Event> findByTypeIgnoreCase(String type);

    List<Event> findByStatus(String status);

    List<Event> findByDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE " +
           "(:department IS NULL OR LOWER(e.department) = LOWER(:department)) AND " +
           "(:type IS NULL OR LOWER(e.type) = LOWER(:type)) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate)")
    List<Event> searchEvents(
            @Param("department") String department,
            @Param("type") String type,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT e FROM Event e WHERE e.date >= :now ORDER BY e.date ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByKeyword(@Param("keyword") String keyword);
}
