package com.example.travel.service;

import com.example.travel.dto.booking.BookingRequest;
import com.example.travel.dto.booking.BookingResponse;
import com.example.travel.dto.booking.BookingStatusUpdateRequest;
import com.example.travel.entity.Booking;
import com.example.travel.entity.TourPackage;
import com.example.travel.entity.User;
import com.example.travel.enums.BookingStatus;
import com.example.travel.enums.PackageStatus;
import com.example.travel.enums.PaymentStatus;
import com.example.travel.exception.BadRequestException;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.BookingRepository;
import com.example.travel.repository.TourPackageRepository;
import com.example.travel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TourPackageRepository tourPackageRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            TourPackageRepository tourPackageRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.tourPackageRepository = tourPackageRepository;
    }

    @Transactional
    public BookingResponse createBooking(String email, BookingRequest request) {
        User user = findUser(email);
        TourPackage tourPackage = tourPackageRepository.findById(request.packageId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tour package not found with id: " + request.packageId()
                ));

        if (tourPackage.getStatus() != PackageStatus.ACTIVE) {
            throw new BadRequestException("This tour package is not currently available");
        }

        BigDecimal total = tourPackage.getPrice()
                .multiply(BigDecimal.valueOf(request.numberOfGuests()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTourPackage(tourPackage);
        booking.setTravelDate(request.travelDate());
        booking.setNumberOfGuests(request.numberOfGuests());
        booking.setTotalAmount(total);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setBookingStatus(BookingStatus.PENDING);

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String email) {
        User user = findUser(email);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BookingResponse cancelMyBooking(String email, Long bookingId) {
        User user = findUser(email);
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId
                ));

        if (booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("A completed booking cannot be cancelled");
        }
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("This booking is already cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BookingResponse updateBookingStatus(
            Long id,
            BookingStatusUpdateRequest request
    ) {
        if (request.bookingStatus() == null && request.paymentStatus() == null) {
            throw new BadRequestException(
                    "At least one booking or payment status must be provided"
            );
        }

        Booking booking = findBooking(id);
        if (request.bookingStatus() != null) {
            booking.setBookingStatus(request.bookingStatus());
        }
        if (request.paymentStatus() != null) {
            booking.setPaymentStatus(request.paymentStatus());
        }
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void deleteBooking(Long id) {
        bookingRepository.delete(findBooking(id));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User account not found"
                ));
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + id
                ));
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getName(),
                booking.getUser().getEmail(),
                booking.getTourPackage().getId(),
                booking.getTourPackage().getName(),
                booking.getTravelDate(),
                booking.getNumberOfGuests(),
                booking.getTotalAmount(),
                booking.getPaymentStatus(),
                booking.getBookingStatus(),
                booking.getCreatedAt()
        );
    }
}
