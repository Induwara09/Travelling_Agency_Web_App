package com.smartpos.service;

import com.smartpos.dto.ShiftDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShiftService {
    private final CashierShiftRepository shifts;private final SaleRepository sales;private final CurrentUserService current;private final AuditService audit;
    public ShiftService(CashierShiftRepository s,SaleRepository sales,CurrentUserService c,AuditService a){shifts=s;this.sales=sales;current=c;audit=a;}
    @Transactional public ShiftView open(OpenShiftRequest r){User u=current.get();if(shifts.findFirstByCashierIdAndStatusOrderByOpenedAtDesc(u.getId(),ShiftStatus.OPEN).isPresent())throw new IllegalArgumentException("You already have an open shift");CashierShift sh=shifts.save(CashierShift.builder().cashier(u).openingCash(r.openingCash()).status(ShiftStatus.OPEN).openedAt(LocalDateTime.now()).build());audit.log(u,"SHIFT_OPENED","SHIFT",sh.getId().toString(),"Opening cash="+r.openingCash());return view(sh);}
    @Transactional public ShiftView close(CloseShiftRequest r){User u=current.get();CashierShift sh=shifts.findFirstByCashierIdAndStatusOrderByOpenedAtDesc(u.getId(),ShiftStatus.OPEN).orElseThrow(()->new IllegalArgumentException("No open shift"));BigDecimal cashSales=sales.cashSalesForCashier(u.getId(),sh.getOpenedAt(),LocalDateTime.now());BigDecimal expected=sh.getOpeningCash().add(cashSales);sh.setExpectedCash(expected);sh.setActualCash(r.actualCash());sh.setDifference(r.actualCash().subtract(expected));sh.setStatus(ShiftStatus.CLOSED);sh.setClosedAt(LocalDateTime.now());shifts.save(sh);audit.log(u,"SHIFT_CLOSED","SHIFT",sh.getId().toString(),"Difference="+sh.getDifference());return view(sh);}
    @Transactional(readOnly=true) public ShiftView current(){User u=current.get();return shifts.findFirstByCashierIdAndStatusOrderByOpenedAtDesc(u.getId(),ShiftStatus.OPEN).map(this::view).orElse(null);}
    @Transactional(readOnly=true) public List<ShiftView> all(){return shifts.findTop100ByOrderByOpenedAtDesc().stream().map(this::view).toList();}
    private ShiftView view(CashierShift s){return new ShiftView(s.getId(),s.getCashier().getName(),s.getOpeningCash(),s.getExpectedCash(),s.getActualCash(),s.getDifference(),s.getStatus().name(),s.getOpenedAt(),s.getClosedAt());}
}
