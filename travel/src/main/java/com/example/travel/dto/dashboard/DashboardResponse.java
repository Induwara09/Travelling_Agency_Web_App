package com.example.travel.dto.dashboard;

import java.math.BigDecimal;

public record DashboardResponse(
        long totalUsers,
        long totalDestinations,
        long totalPackages,
        long totalInquiries,
        long newInquiries,
        long totalBookings,
        long pendingBookings,
        long confirmedBookings,
        BigDecimal totalRevenue
) {
}
