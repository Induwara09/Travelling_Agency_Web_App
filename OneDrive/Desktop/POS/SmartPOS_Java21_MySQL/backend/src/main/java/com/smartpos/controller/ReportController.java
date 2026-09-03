package com.smartpos.controller;

import com.smartpos.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

@RestController @RequestMapping("/api/reports") @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class ReportController {
    private final ReportService service; private final com.smartpos.service.PdfService pdf; private final com.smartpos.service.EmailService email;
    public ReportController(ReportService s,com.smartpos.service.PdfService pdf,com.smartpos.service.EmailService email){service=s;this.pdf=pdf;this.email=email;}
    @GetMapping("/dashboard") public Map<String,Object> dashboard(){return service.dashboard();}
    @GetMapping("/sales") public Map<String,Object> sales(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){return service.salesReport(from,to);}
    @GetMapping("/inventory") public Map<String,Object> inventory(){return service.inventoryReport();}
    @GetMapping("/sales.csv") public ResponseEntity<byte[]> csv(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=sales-report.csv").contentType(MediaType.parseMediaType("text/csv")).body(service.salesCsv(from,to).getBytes(StandardCharsets.UTF_8));}
    @GetMapping("/sales.xlsx") public ResponseEntity<byte[]> xlsx(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=sales-report.xlsx").contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(service.salesExcel(from,to));}
    @PostMapping("/sales/email") public Map<String,Object> emailSales(@RequestBody Map<String,String> body){LocalDate from=LocalDate.parse(body.get("from"));LocalDate to=LocalDate.parse(body.get("to"));String recipient=body.get("recipient");Map<String,Object> r=service.salesReport(from,to);String[] lines={"Period: "+from+" to "+to,"Revenue: Rs. "+r.get("revenue"),"Orders: "+r.get("orders"),"Average Order: Rs. "+r.get("averageOrderValue"),"Cash: Rs. "+r.get("cash"),"Card: Rs. "+r.get("card")};var q=email.queueAttachment(recipient,"SmartPOS Sales Report "+from+" to "+to,String.join("\n",lines),"Sales-Report-"+from+"-"+to+".pdf",pdf.monthlySummary("Sales Report",lines));return Map.of("status",q.getStatus(),"queueId",q.getId());}
    @PostMapping("/inventory/email") public Map<String,Object> emailInventory(@RequestBody Map<String,String> body){String recipient=body.get("recipient");Map<String,Object> r=service.inventoryReport();String[] lines={"Inventory Value: Rs. "+r.get("inventoryValue"),"Low Stock Count: "+r.get("lowStockCount"),"Generated: "+java.time.LocalDateTime.now()};var q=email.queueAttachment(recipient,"SmartPOS Inventory Report",String.join("\n",lines),"Inventory-Report.pdf",pdf.monthlySummary("Inventory Report",lines));return Map.of("status",q.getStatus(),"queueId",q.getId());}

}
