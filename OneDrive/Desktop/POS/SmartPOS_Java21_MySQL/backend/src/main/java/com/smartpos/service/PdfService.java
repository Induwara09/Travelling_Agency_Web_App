package com.smartpos.service;

import com.smartpos.config.AppProperties;
import com.smartpos.model.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {
    private final AppProperties props;
    public PdfService(AppProperties props){this.props=props;}
    public byte[] invoice(Sale sale){
        try(PDDocument doc=new PDDocument(); ByteArrayOutputStream out=new ByteArrayOutputStream()){
            PDPage page=new PDPage(PDRectangle.A4); doc.addPage(page);
            PDFont regular=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont bold=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            try(PDPageContentStream cs=new PDPageContentStream(doc,page)){
                float y=790;
                y=text(cs,bold,18,50,y,props.getBusiness().getName().toUpperCase());
                y=text(cs,regular,9,50,y,props.getBusiness().getAddress());
                y=text(cs,regular,9,50,y,"Tel: "+props.getBusiness().getPhone()+"   Email: "+props.getBusiness().getEmail());
                y-=10; y=text(cs,bold,16,240,y,"TAX INVOICE"); y-=5;
                y=text(cs,regular,10,50,y,"Invoice No: "+sale.getInvoiceNumber());
                y=text(cs,regular,10,50,y,"Date/Time: "+sale.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                y=text(cs,regular,10,50,y,"Cashier: "+sale.getCashier().getName()+"    Order: "+sale.getOrderType()+ (sale.getTableNumber()==null?"":"    Table: "+sale.getTableNumber()));
                if(sale.getCustomer()!=null) y=text(cs,regular,10,50,y,"Customer: "+sale.getCustomer().getName()+"  "+safe(sale.getCustomer().getPhone())+"  "+safe(sale.getCustomer().getEmail()));
                y-=8;
                y=text(cs,bold,9,50,y,String.format("%-9s %-28s %7s %10s %12s","CODE","ITEM","QTY","RATE","AMOUNT"));
                y=line(cs,50,y-2,545); y-=14;
                for(SaleItem i:sale.getItems()){
                    String name=i.getProductName().length()>26?i.getProductName().substring(0,26):i.getProductName();
                    y=text(cs,regular,9,50,y,String.format("%-9s %-28s %7s %10s %12s",i.getItemCode(),name,fmt3(i.getQuantity()),money(i.getUnitPrice()),money(i.getLineTotal())));
                    if(y<160) break;
                }
                y-=8; y=line(cs,320,y,545); y-=16;
                y=amountRow(cs,regular,y,"Subtotal",sale.getSubtotal());
                y=amountRow(cs,regular,y,"Discount",sale.getDiscount().negate());
                y=amountRow(cs,regular,y,"Service Charge",sale.getServiceCharge());
                y=amountRow(cs,regular,y,"Tax",sale.getTax());
                y=text(cs,bold,12,320,y,"TOTAL"); text(cs,bold,12,470,y,"Rs. "+money(sale.getTotal())); y-=18;
                y=amountRow(cs,regular,y,"Paid",sale.getAmountPaid());
                y=amountRow(cs,regular,y,"Balance",sale.getBalance());
                y-=12; y=text(cs,regular,10,50,y,"Payment: "+sale.getPaymentMethod());
                if(sale.isManagerOverride()) y=text(cs,regular,8,50,y,"Manager override: "+safe(sale.getOverrideReason()));
                text(cs,regular,9,50,60,"Thank you for your business. This is a system-generated invoice.");
            }
            doc.save(out); return out.toByteArray();
        }catch(Exception e){throw new IllegalArgumentException("Unable to generate invoice PDF: "+e.getMessage());}
    }
    public byte[] monthlySummary(String title,String[] lines){
        try(PDDocument doc=new PDDocument();ByteArrayOutputStream out=new ByteArrayOutputStream()){
            PDPage page=new PDPage(PDRectangle.A4);doc.addPage(page);PDFont regular=new PDType1Font(Standard14Fonts.FontName.HELVETICA);PDFont bold=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            try(PDPageContentStream cs=new PDPageContentStream(doc,page)){float y=790;y=text(cs,bold,18,50,y,props.getBusiness().getName());y=text(cs,bold,15,50,y,title);y-=12;for(String line:lines){y=text(cs,regular,11,50,y,line);}}
            doc.save(out);return out.toByteArray();
        }catch(Exception e){throw new IllegalArgumentException("Unable to generate report PDF");}
    }
    private float amountRow(PDPageContentStream cs,PDFont f,float y,String label,BigDecimal v)throws Exception{text(cs,f,10,320,y,label);text(cs,f,10,470,y,"Rs. "+money(v));return y-15;}
    private float text(PDPageContentStream cs,PDFont font,float size,float x,float y,String s)throws Exception{cs.beginText();cs.setFont(font,size);cs.newLineAtOffset(x,y);cs.showText(safePdf(s));cs.endText();return y-(size+4);}
    private float line(PDPageContentStream cs,float x,float y,float x2)throws Exception{cs.moveTo(x,y);cs.lineTo(x2,y);cs.stroke();return y;}
    private String money(BigDecimal b){return (b==null?BigDecimal.ZERO:b).setScale(2).toPlainString();}
    private String fmt3(BigDecimal b){return b.stripTrailingZeros().toPlainString();}
    private String safe(String s){return s==null?"":s;}
    private String safePdf(String s){return safe(s).replace("\u20B9","Rs.").replace("\u2013","-").replace("\u2014","-");}
}
