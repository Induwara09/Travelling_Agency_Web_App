package com.example.travel.dto.booking;

import com.example.travel.enums.BookingStatus;
import com.example.travel.enums.PaymentStatus;

public record BookingStatusUpdateRequest(
        BookingStatus bookingStatus,
        PaymentStatus paymentStatus
) {
}
