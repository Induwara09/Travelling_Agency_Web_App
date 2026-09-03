package com.smartpos.repository;
import com.smartpos.model.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RecipeItemRepository extends JpaRepository<RecipeItem,Long>{List<RecipeItem> findByMenuProductIdOrderByIdAsc(Long menuProductId);void deleteByMenuProductId(Long menuProductId);}
