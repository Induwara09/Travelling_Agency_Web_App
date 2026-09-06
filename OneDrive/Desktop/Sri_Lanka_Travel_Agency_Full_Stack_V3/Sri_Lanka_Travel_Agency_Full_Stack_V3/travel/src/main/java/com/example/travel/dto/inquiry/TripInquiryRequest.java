package com.example.travel.dto.inquiry;

import jakarta.validation.constraints.*;

public record TripInquiryRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 150)
        String email,

        @Size(max = 30)
        String phone,

        @Size(max = 100)
        String country,

        @Size(max = 30)
        String travelMonth,

        @NotNull(message = "Number of guests is required")
        @Min(value = 1, message = "At least one guest is required")
        @Max(value = 50, message = "Contact us directly for groups above 50")
        Integer guests,

        @Size(max = 500)
        String interests,

        @NotBlank(message = "Tell us a little about your dream trip")
        @Size(max = 3000)
        String message
) {
}
