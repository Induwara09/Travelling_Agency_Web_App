package com.example.travel.dto.packageinfo;

import com.example.travel.enums.PackageStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TourPackageResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer durationDays,
        String imageUrl,
        String category,
        PackageStatus status,
        Long destinationId,
        String destinationName,
        LocalDateTime createdAt
) {
}
