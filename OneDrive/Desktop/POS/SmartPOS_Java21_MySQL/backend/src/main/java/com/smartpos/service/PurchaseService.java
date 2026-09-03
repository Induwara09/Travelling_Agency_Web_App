package com.smartpos.service;

import com.smartpos.dto.PurchaseDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.util.List;

@Service
public class PurchaseService {
    private final SupplierRepository suppliers;private final PurchaseRepository purchases;private final ProductRepository products;private final StockMovementRepository moves;private final CurrentUserService current;private final AuditService audit;
    public PurchaseService(SupplierRepository s,PurchaseRepository p,ProductRepository pr,StockMovementRepository m,CurrentUserService c,AuditService a){suppliers=s;purchases=p;products=pr;moves=m;current=c;audit=a;}
    @Transactional(readOnly=true) public List<SupplierView> suppliers(){return suppliers.findByActiveTrueOrderByNameAsc().stream().map(this::supplierView).toList();}
    public SupplierView addSupplier(SupplierRequest r){Supplier s=suppliers.save(Supplier.builder().name(r.name()).contactPerson(r.contactPerson()).phone(r.phone()).email(r.email()).address(r.address()).active(r.active()==null||r.active()).build());audit.log(current.get(),"SUPPLIER_CREATED","SUPPLIER",s.getId().toString(),s.getName());return supplierView(s);}
    @Transactional public PurchaseView create(PurchaseRequest r){
        Supplier supplier=suppliers.findById(r.supplierId()).orElseThrow(()->new IllegalArgumentException("Supplier not found"));User u=current.get();Purchase p=Purchase.builder().supplier(supplier).invoiceNumber(r.invoiceNumber()).createdBy(u).total(BigDecimal.ZERO).build();BigDecimal total=BigDecimal.ZERO;
        for(PurchaseItemRequest i:r.items()){Product product=products.findForUpdate(i.productId()).orElseThrow(()->new IllegalArgumentException("Product not found"));BigDecimal line=i.quantity().multiply(i.unitCost()).setScale(2,RoundingMode.HALF_UP);p.getItems().add(PurchaseItem.builder().purchase(p).product(product).quantity(i.quantity()).unitCost(i.unitCost()).lineTotal(line).build());total=total.add(line);BigDecimal before=product.getCurrentStock(),after=before.add(i.quantity());product.setCurrentStock(after);product.setCostPrice(i.unitCost());products.save(product);moves.save(StockMovement.builder().product(product).type(StockMovementType.PURCHASE).quantityChange(i.quantity()).previousStock(before).newStock(after).reason("Purchase "+r.invoiceNumber()).performedBy(u).referenceType("PURCHASE").build());}
        p.setTotal(total);p=purchases.save(p);audit.log(u,"PURCHASE_CREATED","PURCHASE",p.getId().toString(),"Total="+total);return view(p);
    }
    @Transactional(readOnly=true) public List<PurchaseView> recent(){return purchases.findTop100ByOrderByCreatedAtDesc().stream().map(this::view).toList();}
    private SupplierView supplierView(Supplier s){return new SupplierView(s.getId(),s.getName(),s.getContactPerson(),s.getPhone(),s.getEmail(),s.getAddress(),s.isActive());}
    private PurchaseView view(Purchase p){return new PurchaseView(p.getId(),supplierView(p.getSupplier()),p.getInvoiceNumber(),p.getTotal(),p.getCreatedBy()==null?"SYSTEM":p.getCreatedBy().getName(),p.getCreatedAt(),p.getItems().stream().map(i->new PurchaseItemView(i.getProduct().getId(),i.getProduct().getName(),i.getQuantity(),i.getUnitCost(),i.getLineTotal())).toList());}
}
