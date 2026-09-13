package com.example.travel.dto.packageinfo;

import com.example.travel.enums.PackageStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TourPackageRequest(
        @NotBlank(message = "Package name is required")
        @Size(max = 150, message = "Package name cannot exceed 150 characters")
        String name,

        @Size(max = 3000, message = "Description cannot exceed 3000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least one day")
        Integer durationDays,

        @Size(max = 500, message = "Image URL cannot exceed 500 characters")
        String imageUrl,

        @NotBlank(message = "Category is required")
        @Size(max = 50, message = "Category cannot exceed 50 characters")
        String category,

        PackageStatus status,

        @NotNull(message = "Destination ID is required")
        Long destinationId
) {
}
