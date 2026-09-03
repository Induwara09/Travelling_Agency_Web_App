package com.smartpos.repository;
import com.smartpos.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StockMovementRepository extends JpaRepository<StockMovement,Long>{ List<StockMovement> findTop200ByOrderByCreatedAtDesc(); List<StockMovement> findTop100ByProductIdOrderByCreatedAtDesc(Long productId); }
