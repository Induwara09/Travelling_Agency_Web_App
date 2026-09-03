package com.smartpos.controller;

import com.smartpos.model.EmailQueue;
import com.smartpos.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@RestController @RequestMapping("/api/invoices")
public class InvoiceController {
    private final SaleService sales;private final PdfService pdf;private final EmailService email;
    public InvoiceController(SaleService s,PdfService p,EmailService e){sales=s;pdf=p;email=e;}
    @Transactional(readOnly=true) @GetMapping("/{saleId}/pdf") public ResponseEntity<byte[]> pdf(@PathVariable Long saleId){var s=sales.getEntity(saleId);byte[] data=pdf.invoice(s);return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+s.getInvoiceNumber()+".pdf\"").contentType(MediaType.APPLICATION_PDF).body(data);}
    @PostMapping("/{saleId}/email") public Map<String,Object> email(@PathVariable Long saleId,@RequestBody Map<String,String> body){EmailQueue q=email.queueInvoice(saleId,body.get("recipient"));return Map.of("queueId",q.getId(),"status",q.getStatus(),"message",q.getStatus().name().equals("SENT")?"Invoice emailed":"Invoice queued; it will retry automatically when email/internet is available");}
}
