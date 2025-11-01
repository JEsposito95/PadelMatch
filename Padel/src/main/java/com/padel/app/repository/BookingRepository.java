package com.padel.app.repository;

import com.padel.app.model.Booking;
import com.padel.app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
           SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END
           FROM Booking b
           WHERE b.court.id = :courtId
           AND b.status = 'BOOKED'
           AND (
               (b.startTime < :endTime AND b.endTime > :startTime)
           )
           """)
    boolean existsByCourtAndTimeRange(@Param("courtId") Long courtId,
                                      @Param("startTime") java.time.LocalDateTime startTime,
                                      @Param("endTime") java.time.LocalDateTime endTime);

    //Buscar las reservas de un Usuario
    List<Booking> findByCreatedBy(User user);

    //Paginación
    Page<Booking> findByCreatedBy(User user, Pageable pageable);

    Page<Booking> findByCreatedByAndStatus(User user, Booking.Status status, Pageable pageable);
}
