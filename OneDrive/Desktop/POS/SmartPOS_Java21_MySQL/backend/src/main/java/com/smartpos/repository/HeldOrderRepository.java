package com.smartpos.repository;
import com.smartpos.model.HeldOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface HeldOrderRepository extends JpaRepository<HeldOrder,Long>{List<HeldOrder> findTop100ByOrderByCreatedAtDesc();}
