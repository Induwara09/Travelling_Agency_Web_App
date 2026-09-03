package com.smartpos.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
public class RecipeDtos {
 public record RecipeLineRequest(@NotNull Long ingredientProductId,@NotNull @DecimalMin("0.001") BigDecimal quantityPerUnit){}
 public record RecipeRequest(@NotNull Long menuProductId,@NotEmpty List<@Valid RecipeLineRequest> items){}
 public record RecipeLineView(Long id,Long ingredientProductId,String ingredientName,String unit,BigDecimal quantityPerUnit,BigDecimal currentStock){}
 public record RecipeView(Long menuProductId,String menuProductName,List<RecipeLineView> items){}
}
