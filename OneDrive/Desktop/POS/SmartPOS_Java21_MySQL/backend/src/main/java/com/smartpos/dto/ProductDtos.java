package com.smartpos.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductDtos {
    public record CategoryView(Long id, String name, String icon) {}
    public record ProductView(Long id, String externalId, String itemCode, String barcode, String name, String description,
                              CategoryView category, BigDecimal sellingPrice, BigDecimal costPrice, String unit,
                              BigDecimal currentStock, BigDecimal minStock, boolean active, boolean trackInventory,
                              boolean allowDiscount, String imageUrl, String stockStatus) {}
    public record ProductRequest(@NotBlank String itemCode, String barcode, @NotBlank String name, String description,
                                 Long categoryId, @NotNull @DecimalMin("0.0") BigDecimal sellingPrice,
                                 @DecimalMin("0.0") BigDecimal costPrice, String unit, @DecimalMin("0.0") BigDecimal currentStock,
                                 @DecimalMin("0.0") BigDecimal minStock, Boolean active, Boolean trackInventory,
                                 Boolean allowDiscount, String imageUrl) {}
    public record CategoryRequest(@NotBlank String name, String icon, Boolean active) {}
}
