package com.smartpos.service;

import com.smartpos.config.AppProperties;
import com.smartpos.model.*;
import com.smartpos.repository.SyncQueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SyncService {
    private final AppProperties props; private final SyncQueueRepository repo; private final HttpClient client=HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(4)).build();
    public SyncService(AppProperties p,SyncQueueRepository r){props=p;repo=r;}
    @Scheduled(fixedDelay=30000) public void sync(){if(!props.getSync().isEnabled()||props.getSync().getEndpoint()==null||props.getSync().getEndpoint().isBlank())return;repo.findTop50ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(List.of(QueueStatus.PENDING,QueueStatus.FAILED),20).forEach(this::send);}
    private void send(SyncQueue q){try{q.setStatus(QueueStatus.PROCESSING);q.setLastAttemptAt(LocalDateTime.now());q.setRetryCount(q.getRetryCount()+1);repo.save(q);HttpRequest.Builder b=HttpRequest.newBuilder(URI.create(props.getSync().getEndpoint())).timeout(java.time.Duration.ofSeconds(10)).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(q.getPayload()));if(props.getSync().getApiKey()!=null&&!props.getSync().getApiKey().isBlank())b.header("X-API-Key",props.getSync().getApiKey());HttpResponse<String> res=client.send(b.build(),HttpResponse.BodyHandlers.ofString());if(res.statusCode()>=200&&res.statusCode()<300){q.setStatus(QueueStatus.SYNCED);q.setSyncedAt(LocalDateTime.now());q.setLastError(null);}else{q.setStatus(QueueStatus.FAILED);q.setLastError("HTTP "+res.statusCode()+": "+res.body());}}catch(Exception e){q.setStatus(QueueStatus.FAILED);q.setLastError(e.getMessage());}repo.save(q);}
    public long pending(){return repo.countByStatus(QueueStatus.PENDING)+repo.countByStatus(QueueStatus.FAILED);}
}
