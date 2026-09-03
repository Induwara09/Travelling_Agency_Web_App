package com.smartpos.repository;
import com.smartpos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.*;
public interface ProductRepository extends JpaRepository<Product,Long>{
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @Query("select p from Product p where p.id=:id")
 Optional<Product> findForUpdate(@Param("id") Long id);
 Optional<Product> findByBarcode(String barcode);
 Optional<Product> findByItemCodeIgnoreCase(String itemCode);
 List<Product> findByActiveTrueOrderByNameAsc();
 List<Product> findByCategoryIdAndActiveTrueOrderByNameAsc(Long categoryId);
 @Query("select p from Product p where p.active=true and (lower(p.name) like lower(concat('%',:q,'%')) or lower(p.itemCode) like lower(concat('%',:q,'%')) or lower(coalesce(p.barcode,'')) like lower(concat('%',:q,'%'))) order by p.name")
 List<Product> search(@Param("q") String q);
 @Query("select p from Product p where p.trackInventory=true and p.currentStock <= p.minStock order by p.currentStock asc")
 List<Product> findLowStock();
 @Query("select coalesce(sum(p.currentStock * p.costPrice),0) from Product p where p.trackInventory=true")
 BigDecimal inventoryValue();
}
