package com.example.travel.service;

import com.example.travel.dto.dashboard.DashboardResponse;
import com.example.travel.enums.BookingStatus;
import com.example.travel.enums.PaymentStatus;
import com.example.travel.enums.InquiryStatus;
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
    private final ExperienceRepository experienceRepository;
    private final TripInquiryRepository inquiryRepository;

    public DashboardService(
            UserRepository userRepository,
            DestinationRepository destinationRepository,
            TourPackageRepository tourPackageRepository,
            HotelRepository hotelRepository,
            BookingRepository bookingRepository,
            ExperienceRepository experienceRepository,
            TripInquiryRepository inquiryRepository
    ) {
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
        this.experienceRepository = experienceRepository;
        this.inquiryRepository = inquiryRepository;
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
                experienceRepository.count(),
                inquiryRepository.count(),
                inquiryRepository.countByStatus(InquiryStatus.NEW),
                bookingRepository.count(),
                bookingRepository.countByBookingStatus(BookingStatus.PENDING),
                bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED),
                revenue == null ? BigDecimal.ZERO : revenue
        );
    }
}
