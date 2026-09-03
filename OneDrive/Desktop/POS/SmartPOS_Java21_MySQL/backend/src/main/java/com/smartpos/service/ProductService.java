package com.smartpos.service;

import com.smartpos.dto.ProductDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository products; private final CategoryRepository categories; private final StockMovementRepository movements; private final CurrentUserService current; private final AuditService audit;
    public ProductService(ProductRepository p,CategoryRepository c,StockMovementRepository m,CurrentUserService current,AuditService audit){this.products=p;this.categories=c;this.movements=m;this.current=current;this.audit=audit;}
    @Transactional(readOnly=true) public List<ProductView> list(String q,Long categoryId){
        List<Product> list = q!=null&&!q.isBlank()?products.search(q):categoryId!=null?products.findByCategoryIdAndActiveTrueOrderByNameAsc(categoryId):products.findByActiveTrueOrderByNameAsc();
        return list.stream().map(this::view).toList();
    }
    @Transactional(readOnly=true) public List<CategoryView> categories(){ return categories.findByActiveTrueOrderByNameAsc().stream().map(c->new CategoryView(c.getId(),c.getName(),c.getIcon())).toList(); }
    @Transactional public ProductView create(ProductRequest r){
        if(products.findByItemCodeIgnoreCase(r.itemCode()).isPresent()) throw new IllegalArgumentException("Item code already exists");
        Category c=r.categoryId()==null?null:categories.findById(r.categoryId()).orElseThrow(()->new IllegalArgumentException("Category not found"));
        Product p=Product.builder().itemCode(r.itemCode()).barcode(blankToNull(r.barcode())).name(r.name()).description(r.description()).category(c)
                .sellingPrice(nvl(r.sellingPrice())).costPrice(nvl(r.costPrice())).unit(r.unit()==null||r.unit().isBlank()?"EA":r.unit())
                .currentStock(nvl(r.currentStock())).minStock(nvl(r.minStock())).active(r.active()==null||r.active()).trackInventory(r.trackInventory()==null||r.trackInventory())
                .allowDiscount(r.allowDiscount()==null||r.allowDiscount()).imageUrl(r.imageUrl()).build();
        p=products.save(p); User u=current.get();
        if(p.getCurrentStock().compareTo(BigDecimal.ZERO)!=0){ movements.save(StockMovement.builder().product(p).type(StockMovementType.OPENING_STOCK).quantityChange(p.getCurrentStock()).previousStock(BigDecimal.ZERO).newStock(p.getCurrentStock()).reason("Opening stock").performedBy(u).build()); }
        audit.log(u,"PRODUCT_CREATED","PRODUCT",p.getId().toString(),p.getName()); return view(p);
    }
    @Transactional public ProductView update(Long id,ProductRequest r){
        Product p=products.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found"));
        p.setItemCode(r.itemCode()); p.setBarcode(blankToNull(r.barcode())); p.setName(r.name()); p.setDescription(r.description());
        p.setCategory(r.categoryId()==null?null:categories.findById(r.categoryId()).orElseThrow(()->new IllegalArgumentException("Category not found")));
        p.setSellingPrice(nvl(r.sellingPrice())); p.setCostPrice(nvl(r.costPrice())); if(r.unit()!=null) p.setUnit(r.unit()); p.setMinStock(nvl(r.minStock()));
        if(r.active()!=null)p.setActive(r.active()); if(r.trackInventory()!=null)p.setTrackInventory(r.trackInventory()); if(r.allowDiscount()!=null)p.setAllowDiscount(r.allowDiscount()); p.setImageUrl(r.imageUrl());
        products.save(p); audit.log(current.get(),"PRODUCT_UPDATED","PRODUCT",id.toString(),p.getName()); return view(p);
    }
    @Transactional public CategoryView createCategory(CategoryRequest r){
        if(categories.existsByNameIgnoreCase(r.name())) throw new IllegalArgumentException("Category already exists");
        Category c=categories.save(Category.builder().name(r.name()).icon(r.icon()).active(r.active()==null||r.active()).build()); audit.log(current.get(),"CATEGORY_CREATED","CATEGORY",c.getId().toString(),c.getName()); return new CategoryView(c.getId(),c.getName(),c.getIcon());
    }
    public ProductView view(Product p){
        CategoryView c=p.getCategory()==null?null:new CategoryView(p.getCategory().getId(),p.getCategory().getName(),p.getCategory().getIcon());
        String status=!p.isTrackInventory()?"NOT_TRACKED":p.getCurrentStock().compareTo(BigDecimal.ZERO)<=0?"OUT_OF_STOCK":p.getCurrentStock().compareTo(p.getMinStock())<=0?"LOW_STOCK":"HEALTHY";
        return new ProductView(p.getId(),p.getExternalId(),p.getItemCode(),p.getBarcode(),p.getName(),p.getDescription(),c,p.getSellingPrice(),p.getCostPrice(),p.getUnit(),p.getCurrentStock(),p.getMinStock(),p.isActive(),p.isTrackInventory(),p.isAllowDiscount(),p.getImageUrl(),status);
    }
    private BigDecimal nvl(BigDecimal v){return v==null?BigDecimal.ZERO:v;}
    private String blankToNull(String s){return s==null||s.isBlank()?null:s;}
}
