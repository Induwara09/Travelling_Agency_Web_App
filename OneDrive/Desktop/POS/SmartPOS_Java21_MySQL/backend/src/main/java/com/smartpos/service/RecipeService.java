package com.smartpos.service;
import com.smartpos.dto.RecipeDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class RecipeService {
 private final RecipeItemRepository repo;private final ProductRepository products;private final CurrentUserService current;private final AuditService audit;
 public RecipeService(RecipeItemRepository r,ProductRepository p,CurrentUserService c,AuditService a){repo=r;products=p;current=c;audit=a;}
 @Transactional(readOnly=true) public RecipeView get(Long menuProductId){Product p=products.findById(menuProductId).orElseThrow(()->new IllegalArgumentException("Product not found"));return new RecipeView(p.getId(),p.getName(),repo.findByMenuProductIdOrderByIdAsc(menuProductId).stream().map(this::line).toList());}
 @Transactional public RecipeView save(RecipeRequest r){Product menu=products.findById(r.menuProductId()).orElseThrow(()->new IllegalArgumentException("Menu product not found"));repo.deleteByMenuProductId(menu.getId());for(RecipeLineRequest x:r.items()){Product ing=products.findById(x.ingredientProductId()).orElseThrow(()->new IllegalArgumentException("Ingredient product not found"));if(ing.getId().equals(menu.getId()))throw new IllegalArgumentException("Product cannot use itself as an ingredient");repo.save(RecipeItem.builder().menuProduct(menu).ingredientProduct(ing).quantityPerUnit(x.quantityPerUnit()).build());}menu.setTrackInventory(false);products.save(menu);audit.log(current.get(),"RECIPE_UPDATED","PRODUCT",menu.getId().toString(),"Recipe lines="+r.items().size());return get(menu.getId());}
 private RecipeLineView line(RecipeItem r){Product i=r.getIngredientProduct();return new RecipeLineView(r.getId(),i.getId(),i.getName(),i.getUnit(),r.getQuantityPerUnit(),i.getCurrentStock());}
}
