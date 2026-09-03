package com.smartpos.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity
@Table(name="recipe_items", uniqueConstraints=@UniqueConstraint(columnNames={"menu_product_id","ingredient_product_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="menu_product_id",nullable=false) private Product menuProduct;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ingredient_product_id",nullable=false) private Product ingredientProduct;
 @Column(nullable=false,precision=14,scale=3) private BigDecimal quantityPerUnit;
}
