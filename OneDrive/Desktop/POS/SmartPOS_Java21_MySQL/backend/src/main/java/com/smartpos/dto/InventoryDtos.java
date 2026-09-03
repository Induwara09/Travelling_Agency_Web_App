package com.smartpos.dto;

import com.smartpos.model.StockMovementType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryDtos {
    public record StockChangeRequest(@NotNull Long productId, @NotNull BigDecimal quantity, @NotBlank String reason) {}
    public record StockMovementView(Long id, Long productId, String productName, StockMovementType type,
                                    BigDecimal quantityChange, BigDecimal previousStock, BigDecimal newStock,
                                    String reason, String performedBy, String referenceType, String referenceId,
                                    LocalDateTime createdAt) {}
}
