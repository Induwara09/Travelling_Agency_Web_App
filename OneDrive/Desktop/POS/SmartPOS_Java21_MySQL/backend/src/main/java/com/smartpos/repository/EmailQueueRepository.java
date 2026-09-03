package com.smartpos.repository;
import com.smartpos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EmailQueueRepository extends JpaRepository<EmailQueue,Long>{ List<EmailQueue> findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(List<QueueStatus> statuses,int maxRetries); long countByStatus(QueueStatus status); }
