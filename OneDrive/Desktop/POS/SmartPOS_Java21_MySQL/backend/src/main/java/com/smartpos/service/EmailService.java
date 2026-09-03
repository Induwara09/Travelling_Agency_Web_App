package com.smartpos.service;

import com.smartpos.config.AppProperties;
import com.smartpos.model.*;
import com.smartpos.repository.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender mail; private final EmailQueueRepository queue; private final SaleRepository sales; private final PdfService pdf; private final AppProperties props;
    public EmailService(JavaMailSender mail,EmailQueueRepository queue,SaleRepository sales,PdfService pdf,AppProperties props){this.mail=mail;this.queue=queue;this.sales=sales;this.pdf=pdf;this.props=props;}
    @Transactional public EmailQueue queueInvoice(Long saleId,String recipient){
        Sale s=sales.findById(saleId).orElseThrow(()->new IllegalArgumentException("Sale not found"));
        if(recipient==null||recipient.isBlank()) throw new IllegalArgumentException("Recipient email is required");
        EmailQueue q=queue.save(EmailQueue.builder().saleId(saleId).recipient(recipient).subject("Invoice "+s.getInvoiceNumber()+" - "+props.getBusiness().getName()).body("Thank you. Your invoice is attached.").attachmentName(s.getInvoiceNumber()+".pdf").status(QueueStatus.PENDING).build());
        trySend(q); return q;
    }
    @Transactional public EmailQueue queueAttachment(String recipient,String subject,String body,String filename,byte[] attachment){
        if(recipient==null||recipient.isBlank()) throw new IllegalArgumentException("Recipient email is required");
        EmailQueue q=queue.save(EmailQueue.builder().recipient(recipient).subject(subject).body(body).attachmentName(filename).attachmentData(attachment).status(QueueStatus.PENDING).build());
        trySend(q); return q;
    }
    public void sendDirect(String recipient,String subject,String body,String filename,byte[] attachment) throws Exception{
        MimeMessage msg=mail.createMimeMessage(); MimeMessageHelper h=new MimeMessageHelper(msg,true,"UTF-8");
        h.setTo(recipient); h.setSubject(subject); h.setText(body); h.setFrom(props.getBusiness().getEmail()); if(attachment!=null)h.addAttachment(filename,new ByteArrayResource(attachment)); mail.send(msg);
    }
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void retry(){ queue.findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(List.of(QueueStatus.PENDING,QueueStatus.FAILED),10).forEach(this::trySend); }
    public void trySend(EmailQueue q){
        try{ q.setStatus(QueueStatus.PROCESSING); q.setLastAttemptAt(LocalDateTime.now()); q.setRetryCount(q.getRetryCount()+1); queue.save(q);
            byte[] attachment=q.getAttachmentData();
            if(attachment==null && q.getSaleId()!=null){Sale s=sales.findById(q.getSaleId()).orElseThrow();attachment=pdf.invoice(s);}
            sendDirect(q.getRecipient(),q.getSubject(),q.getBody(),q.getAttachmentName(),attachment); q.setStatus(QueueStatus.SENT);q.setSentAt(LocalDateTime.now());q.setLastError(null);
        }catch(Exception e){q.setStatus(QueueStatus.FAILED);q.setLastError(e.getMessage());} queue.save(q);
    }
    public long pendingCount(){return queue.countByStatus(QueueStatus.PENDING)+queue.countByStatus(QueueStatus.FAILED);}
}
