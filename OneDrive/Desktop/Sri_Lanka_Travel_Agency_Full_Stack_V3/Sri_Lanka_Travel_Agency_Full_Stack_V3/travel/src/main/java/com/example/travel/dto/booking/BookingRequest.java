package com.example.travel.dto.booking;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull(message = "Package ID is required")
        Long packageId,

        @NotNull(message = "Travel date is required")
        @FutureOrPresent(message = "Travel date cannot be in the past")
        LocalDate travelDate,

        @NotNull(message = "Number of guests is required")
        @Min(value = 1, message = "At least one guest is required")
        Integer numberOfGuests
) {
}
