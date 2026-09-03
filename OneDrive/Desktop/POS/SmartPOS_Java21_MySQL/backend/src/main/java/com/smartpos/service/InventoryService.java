package com.smartpos.service;

import com.smartpos.dto.InventoryDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryService {
    private final ProductRepository products; private final StockMovementRepository moves; private final CurrentUserService current; private final AuditService audit; private final ProductService productService;
    public InventoryService(ProductRepository p,StockMovementRepository m,CurrentUserService c,AuditService a,ProductService ps){products=p;moves=m;current=c;audit=a;productService=ps;}
    @Transactional(readOnly=true) public List<com.smartpos.dto.ProductDtos.ProductView> lowStock(){ return products.findLowStock().stream().map(productService::view).toList(); }
    @Transactional(readOnly=true) public List<StockMovementView> movements(){ return moves.findTop200ByOrderByCreatedAtDesc().stream().map(this::view).toList(); }
    @Transactional public com.smartpos.dto.ProductDtos.ProductView receive(StockChangeRequest r){ return change(r,StockMovementType.PURCHASE,true); }
    @Transactional public com.smartpos.dto.ProductDtos.ProductView adjust(StockChangeRequest r){ return change(r,StockMovementType.ADJUSTMENT,false); }
    private com.smartpos.dto.ProductDtos.ProductView change(StockChangeRequest r, StockMovementType type, boolean add){
        Product p=products.findById(r.productId()).orElseThrow(()->new IllegalArgumentException("Product not found")); User u=current.get(); BigDecimal before=p.getCurrentStock(); BigDecimal delta=add?r.quantity():r.quantity(); BigDecimal after=before.add(delta);
        p.setCurrentStock(after); products.save(p); moves.save(StockMovement.builder().product(p).type(type).quantityChange(delta).previousStock(before).newStock(after).reason(r.reason()).performedBy(u).build()); audit.log(u,type==StockMovementType.PURCHASE?"STOCK_RECEIVED":"STOCK_ADJUSTED","PRODUCT",p.getId().toString(),"Change "+delta+"; "+r.reason()); return productService.view(p);
    }
    public StockMovementView view(StockMovement m){ return new StockMovementView(m.getId(),m.getProduct().getId(),m.getProduct().getName(),m.getType(),m.getQuantityChange(),m.getPreviousStock(),m.getNewStock(),m.getReason(),m.getPerformedBy()==null?"SYSTEM":m.getPerformedBy().getName(),m.getReferenceType(),m.getReferenceId(),m.getCreatedAt()); }
}
