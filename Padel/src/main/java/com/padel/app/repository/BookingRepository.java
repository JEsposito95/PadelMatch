package com.padel.app.repository;

import com.padel.app.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
