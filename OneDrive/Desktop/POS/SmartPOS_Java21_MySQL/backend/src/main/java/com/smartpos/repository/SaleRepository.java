package com.smartpos.repository;
import com.smartpos.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
public interface SaleRepository extends JpaRepository<Sale,Long>{
 Optional<Sale> findByInvoiceNumber(String invoiceNumber);
 List<Sale> findTop100ByOrderByCreatedAtDesc();
 List<Sale> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime from, LocalDateTime to);
 @Query("select coalesce(sum(s.total),0) from Sale s where s.status <> com.smartpos.model.SaleStatus.VOIDED and s.createdAt between :from and :to")
 BigDecimal totalSales(@Param("from") LocalDateTime from,@Param("to") LocalDateTime to);
 @Query("select count(s) from Sale s where s.status <> com.smartpos.model.SaleStatus.VOIDED and s.createdAt between :from and :to")
 long countSales(@Param("from") LocalDateTime from,@Param("to") LocalDateTime to);
 @Query("select coalesce(sum(s.total),0) from Sale s where s.paymentMethod=:method and s.status <> com.smartpos.model.SaleStatus.VOIDED and s.createdAt between :from and :to")
 BigDecimal totalByPayment(@Param("method") PaymentMethod method,@Param("from") LocalDateTime from,@Param("to") LocalDateTime to);
 @Query("select coalesce(sum(s.total),0) from Sale s where s.cashier.id=:cashierId and s.paymentMethod=com.smartpos.model.PaymentMethod.CASH and s.status <> com.smartpos.model.SaleStatus.VOIDED and s.createdAt between :from and :to")
 BigDecimal cashSalesForCashier(@Param("cashierId") Long cashierId,@Param("from") LocalDateTime from,@Param("to") LocalDateTime to);
}

