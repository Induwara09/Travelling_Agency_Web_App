package com.smartpos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseDtos {
    public record SupplierRequest(@NotBlank String name, String contactPerson, String phone, String email, String address, Boolean active) {}
    public record SupplierView(Long id, String name, String contactPerson, String phone, String email, String address, boolean active) {}
    public record PurchaseItemRequest(@NotNull Long productId, @NotNull @DecimalMin("0.001") BigDecimal quantity, @NotNull @DecimalMin("0") BigDecimal unitCost) {}
    public record PurchaseRequest(@NotNull Long supplierId, String invoiceNumber, @NotEmpty List<@Valid PurchaseItemRequest> items) {}
    public record PurchaseItemView(Long productId, String productName, BigDecimal quantity, BigDecimal unitCost, BigDecimal lineTotal) {}
    public record PurchaseView(Long id, SupplierView supplier, String invoiceNumber, BigDecimal total, String createdBy, LocalDateTime createdAt, List<PurchaseItemView> items) {}
}
