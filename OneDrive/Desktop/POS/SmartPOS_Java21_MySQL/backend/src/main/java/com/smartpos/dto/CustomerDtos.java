package com.smartpos.dto;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
public class CustomerDtos {
 public record CustomerRequest(@NotBlank String name,String phone,String email,String address){}
 public record CustomerView(Long id,String name,String phone,String email,String address,BigDecimal loyaltyPoints){}
}
