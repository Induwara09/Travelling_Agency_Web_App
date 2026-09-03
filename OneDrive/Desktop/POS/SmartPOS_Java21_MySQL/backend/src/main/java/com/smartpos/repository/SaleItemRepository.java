package com.smartpos.repository;
import com.smartpos.model.SaleItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
public interface SaleItemRepository extends JpaRepository<SaleItem,Long>{
 @Query("select si.productName, sum(si.quantity), sum(si.lineTotal) from SaleItem si where si.sale.createdAt between :from and :to and si.sale.status <> com.smartpos.model.SaleStatus.VOIDED group by si.productName order by sum(si.quantity) desc")
 List<Object[]> topProducts(@Param("from") LocalDateTime from,@Param("to") LocalDateTime to);
}
