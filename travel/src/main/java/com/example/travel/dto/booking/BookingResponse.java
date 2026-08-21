package com.example.travel.dto.booking;

import com.example.travel.enums.BookingStatus;
import com.example.travel.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long userId,
        String customerName,
        String customerEmail,
        Long packageId,
        String packageName,
        LocalDate travelDate,
        Integer numberOfGuests,
        BigDecimal totalAmount,
        PaymentStatus paymentStatus,
        BookingStatus bookingStatus,
        LocalDateTime createdAt
) {
}
