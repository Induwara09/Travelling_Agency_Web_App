package com.smartpos.service;

import com.smartpos.config.AppProperties;
import com.smartpos.model.BackupRecord;
import com.smartpos.repository.BackupRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BackupService {
    private final AppProperties props;private final BackupRecordRepository repo;
    @Value("${spring.datasource.url}") private String dbUrl;
    @Value("${spring.datasource.username}") private String dbUser;
    @Value("${spring.datasource.password}") private String dbPassword;
    public BackupService(AppProperties p,BackupRecordRepository r){props=p;repo=r;}
    public List<BackupRecord> recent(){return repo.findTop50ByOrderByCreatedAtDesc();}
    @Scheduled(cron="${smartpos.backup.cron:0 59 23 * * *}",zone="Asia/Colombo") public void scheduled(){if(props.getBackup().isEnabled())create();}
    public BackupRecord create(){
        Path output=null;
        try{
            Path dir=Paths.get(props.getBackup().getDirectory());Files.createDirectories(dir);String name="smartpos_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"))+".sql";output=dir.resolve(name);
            String dbName=databaseName(dbUrl);ProcessBuilder pb=new ProcessBuilder(props.getBackup().getMysqldump(),"-h","localhost","-u"+dbUser,"--single-transaction","--routines","--events",dbName);pb.environment().put("MYSQL_PWD",dbPassword);pb.redirectOutput(output.toFile());pb.redirectError(ProcessBuilder.Redirect.PIPE);Process p=pb.start();int exit=p.waitFor();String err=new String(p.getErrorStream().readAllBytes());if(exit!=0)throw new IllegalStateException(err.isBlank()?"mysqldump failed with exit code "+exit:err);
            return repo.save(BackupRecord.builder().filePath(output.toString()).status("SUCCESS").sizeBytes(Files.size(output)).message("Backup created successfully").build());
        }catch(Exception e){return repo.save(BackupRecord.builder().filePath(output==null?props.getBackup().getDirectory():output.toString()).status("FAILED").sizeBytes(0L).message(e.getMessage()).build());}
    }
    private String databaseName(String url){String s=url.substring(url.indexOf("//")+2);s=s.substring(s.indexOf('/')+1);int q=s.indexOf('?');return q>=0?s.substring(0,q):s;}
}
