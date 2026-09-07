package com.example.travel.dto.inquiry;

import com.example.travel.enums.InquiryStatus;
import jakarta.validation.constraints.NotNull;

public record InquiryStatusUpdateRequest(
        @NotNull(message = "Inquiry status is required") InquiryStatus status
) {
}
