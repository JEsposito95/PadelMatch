package com.padel.app.service;

import com.padel.app.dto.statistics.AdminStatsDTO;
import com.padel.app.dto.statistics.TopCourtDTO;
import com.padel.app.model.Booking;
import com.padel.app.repository.BookingRepository;
import com.padel.app.repository.CourtRepository;
import com.padel.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatisticsService {

    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final BookingRepository bookingRepository;

    public StatisticsService(UserRepository userRepository,
                             CourtRepository courtRepository,
                             BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.courtRepository = courtRepository;
        this.bookingRepository = bookingRepository;
    }

    public AdminStatsDTO getAdminStatistics() {
        long totalUsers = userRepository.count();
        long totalCourts = courtRepository.count();
        long totalBookings = bookingRepository.count();

        double avgBookingsPerUser = totalUsers == 0
                ? 0
                : (double) totalBookings / totalUsers;

        // Suma total de precios de las reservas (si el booking tiene cancha asociada con precio)
        List<Booking> bookings = bookingRepository.findAll();
        BigDecimal totalIncome = bookings.stream()
                .filter(b -> b.getCourt() != null && b.getCourt().getPrice() != null)
                .map(b -> b.getCourt().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminStatsDTO(
                totalUsers,
                totalCourts,
                totalBookings,
                avgBookingsPerUser,
                totalIncome
        );
    }

    public List<TopCourtDTO> getTopCourts() {
        return bookingRepository.findTopCourts();
    }

}
