package com.smartpos.service;

import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportService {
    private final SaleRepository sales; private final SaleItemRepository items; private final ProductRepository products;
    public ReportService(SaleRepository sales,SaleItemRepository items,ProductRepository products){this.sales=sales;this.items=items;this.products=products;}
    @Transactional(readOnly=true) public Map<String,Object> dashboard(){
        LocalDateTime from=LocalDate.now().atStartOfDay(), to=LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1);
        BigDecimal revenue=sales.totalSales(from,to); long orders=sales.countSales(from,to); BigDecimal avg=orders==0?BigDecimal.ZERO:revenue.divide(BigDecimal.valueOf(orders),2,RoundingMode.HALF_UP);
        return Map.of("todayRevenue",revenue,"todayOrders",orders,"averageOrderValue",avg,"cashSales",sales.totalByPayment(PaymentMethod.CASH,from,to),"cardSales",sales.totalByPayment(PaymentMethod.CARD,from,to),"inventoryValue",products.inventoryValue(),"lowStockCount",products.findLowStock().size(),"topProducts",top(from,to,8));
    }
    @Transactional(readOnly=true) public Map<String,Object> salesReport(LocalDate fromDate,LocalDate toDate){
        LocalDateTime from=fromDate.atStartOfDay(), to=toDate.plusDays(1).atStartOfDay().minusNanos(1); BigDecimal revenue=sales.totalSales(from,to); long count=sales.countSales(from,to);
        List<Map<String,Object>> rows=sales.findByCreatedAtBetweenOrderByCreatedAtDesc(from,to).stream().map(s->{Map<String,Object> m=new LinkedHashMap<>();m.put("id",s.getId());m.put("invoice",s.getInvoiceNumber());m.put("date",s.getCreatedAt());m.put("cashier",s.getCashier().getName());m.put("payment",s.getPaymentMethod());m.put("total",s.getTotal());m.put("status",s.getStatus());return m;}).toList();
        Map<String,Object> result=new LinkedHashMap<>();result.put("from",fromDate);result.put("to",toDate);result.put("revenue",revenue);result.put("orders",count);result.put("averageOrderValue",count==0?BigDecimal.ZERO:revenue.divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_UP));result.put("cash",sales.totalByPayment(PaymentMethod.CASH,from,to));result.put("card",sales.totalByPayment(PaymentMethod.CARD,from,to));result.put("topProducts",top(from,to,20));result.put("rows",rows);return result;
    }
    @Transactional(readOnly=true) public Map<String,Object> inventoryReport(){
        List<Map<String,Object>> rows=products.findAll().stream().sorted(Comparator.comparing(Product::getName)).map(p->{Map<String,Object> m=new LinkedHashMap<>();m.put("id",p.getId());m.put("code",p.getItemCode());m.put("product",p.getName());m.put("stock",p.getCurrentStock());m.put("minStock",p.getMinStock());m.put("costPrice",p.getCostPrice());m.put("stockValue",p.getCurrentStock().multiply(p.getCostPrice()).setScale(2,RoundingMode.HALF_UP));m.put("status",p.getCurrentStock().compareTo(BigDecimal.ZERO)<=0?"OUT_OF_STOCK":p.getCurrentStock().compareTo(p.getMinStock())<=0?"LOW_STOCK":"HEALTHY");return m;}).toList();
        return Map.of("inventoryValue",products.inventoryValue(),"lowStockCount",products.findLowStock().size(),"rows",rows);
    }
    @Transactional(readOnly=true) public byte[] salesExcel(LocalDate from,LocalDate to){
        Map<String,Object> r=salesReport(from,to);
        try(Workbook wb=new XSSFWorkbook();ByteArrayOutputStream out=new ByteArrayOutputStream()){
            Sheet s=wb.createSheet("Sales");Row h=s.createRow(0);String[] headers={"Invoice","Date","Cashier","Payment","Status","Total"};for(int i=0;i<headers.length;i++)h.createCell(i).setCellValue(headers[i]);
            @SuppressWarnings("unchecked") List<Map<String,Object>> rows=(List<Map<String,Object>>)r.get("rows");int rowNo=1;for(Map<String,Object> row:rows){Row rr=s.createRow(rowNo++);rr.createCell(0).setCellValue(String.valueOf(row.get("invoice")));rr.createCell(1).setCellValue(String.valueOf(row.get("date")));rr.createCell(2).setCellValue(String.valueOf(row.get("cashier")));rr.createCell(3).setCellValue(String.valueOf(row.get("payment")));rr.createCell(4).setCellValue(String.valueOf(row.get("status")));rr.createCell(5).setCellValue(((BigDecimal)row.get("total")).doubleValue());}
            for(int i=0;i<headers.length;i++)s.autoSizeColumn(i);wb.write(out);return out.toByteArray();
        }catch(Exception e){throw new IllegalArgumentException("Could not create Excel report");}
    }
    @Transactional(readOnly=true) public String salesCsv(LocalDate from,LocalDate to){
        StringBuilder b=new StringBuilder("Invoice,Date,Cashier,Payment,Status,Total\n");
        sales.findByCreatedAtBetweenOrderByCreatedAtDesc(from.atStartOfDay(),to.plusDays(1).atStartOfDay().minusNanos(1)).forEach(s->b.append(s.getInvoiceNumber()).append(',').append(s.getCreatedAt()).append(',').append(csv(s.getCashier().getName())).append(',').append(s.getPaymentMethod()).append(',').append(s.getStatus()).append(',').append(s.getTotal()).append('\n'));return b.toString();
    }
    private List<Map<String,Object>> top(LocalDateTime f,LocalDateTime t,int limit){ return items.topProducts(f,t).stream().limit(limit).map(r->Map.of("product",r[0],"quantity",r[1],"revenue",r[2])).toList(); }
    private String csv(String v){return '"'+v.replace("\"","\"\"")+'"';}
}
