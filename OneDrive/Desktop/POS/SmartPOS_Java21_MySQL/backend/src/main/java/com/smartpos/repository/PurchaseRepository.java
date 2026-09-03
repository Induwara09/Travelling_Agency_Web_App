package com.smartpos.repository;
import com.smartpos.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PurchaseRepository extends JpaRepository<Purchase,Long>{ List<Purchase> findTop100ByOrderByCreatedAtDesc(); }
