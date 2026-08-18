package com.example.travel.service;

import com.example.travel.dto.dashboard.DashboardResponse;
import com.example.travel.enums.BookingStatus;
import com.example.travel.enums.PaymentStatus;
import com.example.travel.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final TourPackageRepository tourPackageRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    public DashboardService(
            UserRepository userRepository,
            DestinationRepository destinationRepository,
            TourPackageRepository tourPackageRepository,
            HotelRepository hotelRepository,
            BookingRepository bookingRepository
    ) {
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        BigDecimal revenue = bookingRepository
                .sumTotalAmountByPaymentStatus(PaymentStatus.PAID);

        return new DashboardResponse(
                userRepository.count(),
                destinationRepository.count(),
                tourPackageRepository.count(),
                hotelRepository.count(),
                bookingRepository.count(),
                bookingRepository.countByBookingStatus(BookingStatus.PENDING),
                bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED),
                revenue == null ? BigDecimal.ZERO : revenue
        );
    }
}
