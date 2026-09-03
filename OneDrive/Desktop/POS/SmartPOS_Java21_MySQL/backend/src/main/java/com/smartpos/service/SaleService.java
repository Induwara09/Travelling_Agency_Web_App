package com.smartpos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpos.config.AppProperties;
import com.smartpos.dto.SaleDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SaleService {
    private final SaleRepository sales; private final ProductRepository products; private final CustomerRepository customers;
    private final StockMovementRepository stockMoves; private final UserRepository users; private final CurrentUserService current;
    private final PasswordEncoder encoder; private final AuditService audit; private final SyncQueueRepository syncQueue; private final AppProperties props; private final ObjectMapper mapper; private final RecipeItemRepository recipes;
    public SaleService(SaleRepository sales,ProductRepository products,CustomerRepository customers,StockMovementRepository stockMoves,UserRepository users,CurrentUserService current,PasswordEncoder encoder,AuditService audit,SyncQueueRepository syncQueue,AppProperties props,ObjectMapper mapper,RecipeItemRepository recipes){
        this.sales=sales;this.products=products;this.customers=customers;this.stockMoves=stockMoves;this.users=users;this.current=current;this.encoder=encoder;this.audit=audit;this.syncQueue=syncQueue;this.props=props;this.mapper=mapper;this.recipes=recipes;
    }

    @Transactional
    public SaleView checkout(CheckoutRequest r){
        User cashier=current.get();
        List<PreparedItem> prepared=new ArrayList<>(); boolean needsOverride=false; StringBuilder shortage=new StringBuilder();
        BigDecimal subtotal=BigDecimal.ZERO; BigDecimal lineDiscountTotal=BigDecimal.ZERO;
        Map<Long,BigDecimal> ingredientNeeds=new LinkedHashMap<>();
        Map<Long,Product> ingredientProducts=new LinkedHashMap<>();

        for(CheckoutItem req:r.items()){
            Product p=products.findForUpdate(req.productId()).orElseThrow(()->new IllegalArgumentException("Product not found: "+req.productId()));
            if(!p.isActive()) throw new IllegalArgumentException(p.getName()+" is inactive");
            BigDecimal qty=req.quantity(); BigDecimal disc=req.discount()==null?BigDecimal.ZERO:req.discount();
            if(disc.compareTo(BigDecimal.ZERO)>0 && !p.isAllowDiscount()) throw new IllegalArgumentException("Discount is not allowed for "+p.getName());
            List<RecipeItem> recipe=recipes.findByMenuProductIdOrderByIdAsc(p.getId());
            if(recipe.isEmpty()){
                if(p.isTrackInventory() && p.getCurrentStock().compareTo(qty)<0){ needsOverride=true; shortage.append(p.getName()).append(" available ").append(p.getCurrentStock()).append(", requested ").append(qty).append("; "); }
            } else {
                for(RecipeItem ri:recipe){
                    Long ingredientId=ri.getIngredientProduct().getId();
                    Product ingredient=ingredientProducts.computeIfAbsent(ingredientId,id->products.findForUpdate(id).orElseThrow(()->new IllegalArgumentException("Ingredient not found")));
                    BigDecimal need=ri.getQuantityPerUnit().multiply(qty);
                    ingredientNeeds.merge(ingredientId,need,BigDecimal::add);
                }
            }
            BigDecimal base=p.getSellingPrice().multiply(qty).setScale(2, RoundingMode.HALF_UP);
            if(disc.compareTo(base)>0) throw new IllegalArgumentException("Discount exceeds line amount for "+p.getName());
            BigDecimal lineTotal=base.subtract(disc); subtotal=subtotal.add(base); lineDiscountTotal=lineDiscountTotal.add(disc);
            prepared.add(new PreparedItem(p,qty,disc,lineTotal,recipe));
        }
        for(var e:ingredientNeeds.entrySet()){
            Product ingredient=ingredientProducts.get(e.getKey());
            if(ingredient.isTrackInventory() && ingredient.getCurrentStock().compareTo(e.getValue())<0){needsOverride=true;shortage.append(ingredient.getName()).append(" ingredient available ").append(ingredient.getCurrentStock()).append(", required ").append(e.getValue()).append("; ");}
        }

        User approver=null;
        if(needsOverride){
            if(r.managerUsername()==null||r.managerPassword()==null) throw new IllegalArgumentException("Insufficient stock: "+shortage+" Manager authorization required.");
            approver=users.findByUsernameIgnoreCase(r.managerUsername()).orElseThrow(()->new IllegalArgumentException("Manager account not found"));
            if(!approver.isActive() || (approver.getRole()!=RoleName.MANAGER && approver.getRole()!=RoleName.ADMIN) || !encoder.matches(r.managerPassword(),approver.getPasswordHash())) throw new IllegalArgumentException("Manager authorization failed");
            if(r.overrideReason()==null||r.overrideReason().isBlank()) throw new IllegalArgumentException("Override reason is required");
        }
        BigDecimal billDiscount=nvl(r.billDiscount()); BigDecimal totalDiscount=lineDiscountTotal.add(billDiscount); BigDecimal net=subtotal.subtract(totalDiscount);
        if(net.compareTo(BigDecimal.ZERO)<0) throw new IllegalArgumentException("Discount exceeds order value");
        BigDecimal service=nvl(r.serviceCharge()); BigDecimal tax=nvl(r.tax()); BigDecimal total=net.add(service).add(tax).setScale(2,RoundingMode.HALF_UP);
        if(r.amountReceived().compareTo(total)<0 && r.paymentMethod()==PaymentMethod.CASH) throw new IllegalArgumentException("Cash received is less than total");
        BigDecimal paid=r.paymentMethod()==PaymentMethod.CASH?r.amountReceived():total; BigDecimal balance=paid.subtract(total).max(BigDecimal.ZERO); Customer customer=resolveCustomer(r.customer());
        Sale sale=Sale.builder().externalId(UUID.randomUUID().toString()).invoiceNumber(nextInvoiceNumber()).orderType(blankDefault(r.orderType(),"TAKEAWAY")).tableNumber(r.tableNumber()).customer(customer).cashier(cashier)
                .subtotal(subtotal.setScale(2,RoundingMode.HALF_UP)).discount(totalDiscount.setScale(2,RoundingMode.HALF_UP)).serviceCharge(service).tax(tax).total(total).amountPaid(paid).balance(balance)
                .paymentMethod(r.paymentMethod()).status(SaleStatus.COMPLETED).managerOverride(needsOverride).overrideReason(needsOverride?r.overrideReason():null).approvedBy(approver).build();

        for(PreparedItem pi:prepared){
            Product p=pi.product();
            if(pi.recipe().isEmpty() && p.isTrackInventory()) deductStock(p,pi.qty(),cashier,"Sale "+sale.getInvoiceNumber(),sale.getExternalId(),StockMovementType.SALE);
            if(!pi.recipe().isEmpty()){
                for(RecipeItem ri:pi.recipe()){
                    Product ingredient=ingredientProducts.get(ri.getIngredientProduct().getId());
                    BigDecimal consume=ri.getQuantityPerUnit().multiply(pi.qty()); if(ingredient.isTrackInventory()) deductStock(ingredient,consume,cashier,"Recipe consumption for "+p.getName()+" / "+sale.getInvoiceNumber(),sale.getExternalId(),StockMovementType.SALE);
                }
            }
            SaleItem si=SaleItem.builder().sale(sale).product(p).productName(p.getName()).itemCode(p.getItemCode()).quantity(pi.qty()).unitPrice(p.getSellingPrice()).discount(pi.discount()).tax(BigDecimal.ZERO).lineTotal(pi.lineTotal()).build(); sale.getItems().add(si);
        }
        sale.getPayments().add(Payment.builder().sale(sale).method(r.paymentMethod()).amount(total).reference(r.paymentReference()).build()); sale=sales.save(sale);
        audit.log(cashier,"SALE_COMPLETED","SALE",sale.getId().toString(),sale.getInvoiceNumber()+" total="+sale.getTotal());
        if(needsOverride) audit.log(approver,"NEGATIVE_STOCK_OVERRIDE","SALE",sale.getId().toString(),"Cashier="+cashier.getUsername()+" reason="+r.overrideReason()+" shortages="+shortage);
        if(props.getSync().isEnabled()) enqueueSync(sale); return view(sale);
    }

    private void deductStock(Product p,BigDecimal qty,User u,String reason,String ref,StockMovementType type){BigDecimal before=p.getCurrentStock(),after=before.subtract(qty);p.setCurrentStock(after);products.save(p);stockMoves.save(StockMovement.builder().product(p).type(type).quantityChange(qty.negate()).previousStock(before).newStock(after).reason(reason).performedBy(u).referenceType("SALE").referenceId(ref).build());}
    @Transactional(readOnly=true) public List<SaleView> recent(){ return sales.findTop100ByOrderByCreatedAtDesc().stream().map(this::view).toList(); }
    @Transactional(readOnly=true) public SaleView get(Long id){ return view(getEntity(id)); }
    public Sale getEntity(Long id){ return sales.findById(id).orElseThrow(()->new IllegalArgumentException("Sale not found")); }

    @Transactional public SaleView voidSale(Long id,String reason){
        User u=current.get(); if(u.getRole()==RoleName.CASHIER) throw new IllegalArgumentException("Manager/Admin approval required"); Sale s=getEntity(id); if(s.getStatus()==SaleStatus.VOIDED) return view(s);
        for(SaleItem item:s.getItems()){
            List<RecipeItem> recipe=recipes.findByMenuProductIdOrderByIdAsc(item.getProduct().getId());
            if(recipe.isEmpty() && item.getProduct().isTrackInventory()) returnStock(item.getProduct(),item.getQuantity(),u,"Void "+s.getInvoiceNumber()+": "+reason,s.getExternalId());
            else for(RecipeItem ri:recipe) if(ri.getIngredientProduct().isTrackInventory()) returnStock(ri.getIngredientProduct(),ri.getQuantityPerUnit().multiply(item.getQuantity()),u,"Recipe return for void "+s.getInvoiceNumber(),s.getExternalId());
        }
        s.setStatus(SaleStatus.VOIDED); sales.save(s); audit.log(u,"SALE_VOIDED","SALE",id.toString(),reason); return view(s);
    }
    private void returnStock(Product source,BigDecimal qty,User u,String reason,String ref){Product p=products.findForUpdate(source.getId()).orElseThrow();BigDecimal before=p.getCurrentStock(),after=before.add(qty);p.setCurrentStock(after);products.save(p);stockMoves.save(StockMovement.builder().product(p).type(StockMovementType.REFUND).quantityChange(qty).previousStock(before).newStock(after).reason(reason).performedBy(u).referenceType("SALE").referenceId(ref).build());}

    public SaleView view(Sale s){
        List<SaleItemView> items=s.getItems().stream().map(i->new SaleItemView(i.getProduct().getId(),i.getItemCode(),i.getProductName(),i.getQuantity(),i.getUnitPrice(),i.getDiscount(),i.getTax(),i.getLineTotal())).toList();
        return new SaleView(s.getId(),s.getInvoiceNumber(),s.getExternalId(),s.getOrderType(),s.getTableNumber(),s.getCustomer()==null?null:s.getCustomer().getName(),s.getCustomer()==null?null:s.getCustomer().getPhone(),s.getCustomer()==null?null:s.getCustomer().getEmail(),s.getCashier().getName(),items,s.getSubtotal(),s.getDiscount(),s.getServiceCharge(),s.getTax(),s.getTotal(),s.getAmountPaid(),s.getBalance(),s.getPaymentMethod(),s.getStatus(),s.isManagerOverride(),s.getOverrideReason(),s.getApprovedBy()==null?null:s.getApprovedBy().getName(),s.getCreatedAt());
    }
    private Customer resolveCustomer(CustomerInput c){if(c==null||((c.name()==null||c.name().isBlank())&&(c.phone()==null||c.phone().isBlank())&&(c.email()==null||c.email().isBlank())))return null;if(c.phone()!=null&&!c.phone().isBlank()){Optional<Customer> ex=customers.findFirstByPhone(c.phone());if(ex.isPresent())return ex.get();}if(c.email()!=null&&!c.email().isBlank()){Optional<Customer> ex=customers.findFirstByEmailIgnoreCase(c.email());if(ex.isPresent())return ex.get();}return customers.save(Customer.builder().name(blankDefault(c.name(),"Walk-in Customer")).phone(c.phone()).email(c.email()).address(c.address()).build());}
    private void enqueueSync(Sale s){try{syncQueue.save(SyncQueue.builder().entityType("SALE").entityId(s.getExternalId()).operationType("CREATE").payload(mapper.writeValueAsString(view(s))).status(QueueStatus.PENDING).build());}catch(Exception e){audit.log(null,"SYNC_QUEUE_ERROR","SALE",s.getId().toString(),e.getMessage());}}
    private String nextInvoiceNumber(){return props.getBusiness().getInvoicePrefix()+"-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"));}
    private BigDecimal nvl(BigDecimal b){return b==null?BigDecimal.ZERO:b;} private String blankDefault(String s,String d){return s==null||s.isBlank()?d:s;}
    private record PreparedItem(Product product,BigDecimal qty,BigDecimal discount,BigDecimal lineTotal,List<RecipeItem> recipe){}
}
