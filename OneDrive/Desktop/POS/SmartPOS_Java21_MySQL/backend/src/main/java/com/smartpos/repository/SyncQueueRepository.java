package com.smartpos.repository;
import com.smartpos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SyncQueueRepository extends JpaRepository<SyncQueue,Long>{ List<SyncQueue> findTop50ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(List<QueueStatus> statuses,int maxRetries); long countByStatus(QueueStatus status); }
