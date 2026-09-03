package com.smartpos.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class ShiftDtos {
 public record OpenShiftRequest(@NotNull @DecimalMin("0") BigDecimal openingCash){}
 public record CloseShiftRequest(@NotNull @DecimalMin("0") BigDecimal actualCash){}
 public record ShiftView(Long id,String cashier,BigDecimal openingCash,BigDecimal expectedCash,BigDecimal actualCash,BigDecimal difference,String status,LocalDateTime openedAt,LocalDateTime closedAt){}
}
