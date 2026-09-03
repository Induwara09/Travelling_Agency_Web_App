package com.smartpos.service;

import com.smartpos.config.AppProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class MonthlyReportJob {
    private final AppProperties props; private final ReportService reports; private final PdfService pdf; private final EmailService email;
    public MonthlyReportJob(AppProperties props,ReportService reports,PdfService pdf,EmailService email){this.props=props;this.reports=reports;this.pdf=pdf;this.email=email;}
    @Scheduled(cron="${smartpos.reports.monthly-cron:0 0 8 1 * *}", zone="Asia/Colombo")
    public void sendMonthly(){
        if(props.getReports().getRecipients()==null||props.getReports().getRecipients().isBlank())return;
        YearMonth ym=YearMonth.now().minusMonths(1);String label=ym.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+ym.getYear();
        Map<String,Object> s=reports.salesReport(ym.atDay(1),ym.atEndOfMonth());
        String[] salesLines={"Period: "+label,"Revenue: Rs. "+s.get("revenue"),"Orders: "+s.get("orders"),"Average Order: Rs. "+s.get("averageOrderValue"),"Cash: Rs. "+s.get("cash"),"Card: Rs. "+s.get("card")};
        byte[] salesPdf=pdf.monthlySummary("Monthly Sales Report - "+label,salesLines);
        Map<String,Object> inv=reports.inventoryReport();
        @SuppressWarnings("unchecked") List<Map<String,Object>> invRows=(List<Map<String,Object>>)inv.get("rows");
        long out=invRows.stream().filter(r->"OUT_OF_STOCK".equals(r.get("status"))).count();long low=invRows.stream().filter(r->"LOW_STOCK".equals(r.get("status"))).count();long negative=invRows.stream().filter(r->new java.math.BigDecimal(String.valueOf(r.get("stock"))).signum()<0).count();
        String[] inventoryLines={"Report month: "+label,"Current Inventory Value: Rs. "+inv.get("inventoryValue"),"Low Stock Items: "+low,"Out of Stock Items: "+out,"Negative Stock Items: "+negative,"Generated: "+LocalDateTime.now()};
        byte[] inventoryPdf=pdf.monthlySummary("Monthly Inventory Report - "+label,inventoryLines);
        for(String recipient:props.getReports().getRecipients().split(",")){
            String to=recipient.trim(); if(to.isBlank())continue;
            email.queueAttachment(to,props.getBusiness().getName()+" - Monthly Sales Report - "+label,String.join("\n",salesLines),"Monthly-Sales-"+ym+".pdf",salesPdf);
            email.queueAttachment(to,props.getBusiness().getName()+" - Monthly Inventory Report - "+label,String.join("\n",inventoryLines),"Monthly-Inventory-"+ym+".pdf",inventoryPdf);
        }
    }
}
