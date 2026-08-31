package com.agendastyle.backend.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.employee.id = :employeeId
        AND a.status <> :excludedStatus
        AND a.startDateTime < :endDateTime
        AND a.endDateTime > :startDateTime
        """)
    boolean existsOverlappingAppointment(
        @Param("employeeId") Long employeeId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime,
        @Param("excludedStatus") AppointmentStatus excludedStatus
    );

    @Query("""
        SELECT a
        FROM Appointment a
        WHERE a.employee.id = :employeeId
        AND a.startDateTime >= :startOfDay
        AND a.startDateTime < :startOfNextDay
        ORDER BY a.startDateTime ASC
        """)
    List<Appointment> findDailyAgenda(
        @Param("employeeId") Long employeeId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("startOfNextDay") LocalDateTime startOfNextDay
    );
}