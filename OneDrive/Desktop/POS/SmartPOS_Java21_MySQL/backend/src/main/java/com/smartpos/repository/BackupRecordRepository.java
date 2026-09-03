package com.smartpos.repository;
import com.smartpos.model.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BackupRecordRepository extends JpaRepository<BackupRecord,Long>{ List<BackupRecord> findTop50ByOrderByCreatedAtDesc(); }
