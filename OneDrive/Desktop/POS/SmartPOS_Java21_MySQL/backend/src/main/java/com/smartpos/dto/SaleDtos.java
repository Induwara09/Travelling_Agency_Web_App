package com.smartpos.dto;

import com.smartpos.model.PaymentMethod;
import com.smartpos.model.SaleStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleDtos {
    public record CheckoutItem(@NotNull Long productId, @NotNull @DecimalMin("0.001") BigDecimal quantity,
                               @DecimalMin("0.0") BigDecimal discount) {}
    public record CustomerInput(String name, String phone, String email, String address) {}
    public record CheckoutRequest(@NotEmpty List<@Valid CheckoutItem> items,
                                  String orderType, String tableNumber, CustomerInput customer,
                                  @DecimalMin("0.0") BigDecimal billDiscount,
                                  @DecimalMin("0.0") BigDecimal serviceCharge,
                                  @DecimalMin("0.0") BigDecimal tax,
                                  @NotNull PaymentMethod paymentMethod,
                                  @NotNull @DecimalMin("0.0") BigDecimal amountReceived,
                                  String paymentReference,
                                  String managerUsername, String managerPassword, String overrideReason) {}
    public record SaleItemView(Long productId, String itemCode, String productName, BigDecimal quantity,
                               BigDecimal unitPrice, BigDecimal discount, BigDecimal tax, BigDecimal lineTotal) {}
    public record SaleView(Long id, String invoiceNumber, String externalId, String orderType, String tableNumber,
                           String customerName, String customerPhone, String customerEmail, String cashierName,
                           List<SaleItemView> items, BigDecimal subtotal, BigDecimal discount,
                           BigDecimal serviceCharge, BigDecimal tax, BigDecimal total, BigDecimal amountPaid,
                           BigDecimal balance, PaymentMethod paymentMethod, SaleStatus status,
                           boolean managerOverride, String overrideReason, String approvedBy,
                           LocalDateTime createdAt) {}
}
