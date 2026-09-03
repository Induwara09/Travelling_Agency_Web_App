package com.smartpos.repository;
import com.smartpos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CashierShiftRepository extends JpaRepository<CashierShift,Long>{ Optional<CashierShift> findFirstByCashierIdAndStatusOrderByOpenedAtDesc(Long cashierId, ShiftStatus status); List<CashierShift> findTop100ByOrderByOpenedAtDesc(); }
