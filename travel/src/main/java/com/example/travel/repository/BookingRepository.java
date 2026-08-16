package com.example.travel.repository;

import com.example.travel.entity.Booking;
import com.example.travel.enums.BookingStatus;
import com.example.travel.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findAllByOrderByCreatedAtDesc();

    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    long countByBookingStatus(BookingStatus bookingStatus);

    @Query("select coalesce(sum(b.totalAmount), 0) from Booking b " +
            "where b.paymentStatus = :paymentStatus")
    BigDecimal sumTotalAmountByPaymentStatus(
            @Param("paymentStatus") PaymentStatus paymentStatus
    );
}
