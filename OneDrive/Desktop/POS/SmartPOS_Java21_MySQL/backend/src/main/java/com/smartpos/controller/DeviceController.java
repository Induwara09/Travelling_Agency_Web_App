package com.smartpos.controller;
import com.smartpos.config.AppProperties;
import com.smartpos.model.Device;
import com.smartpos.repository.DeviceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
@RestController @RequestMapping("/api/devices")
public class DeviceController {
 private final DeviceRepository repo;private final AppProperties props;
 public DeviceController(DeviceRepository r,AppProperties p){repo=r;props=p;}
 public record Heartbeat(String terminalId,String deviceName,Double cpuPercent,Double ramPercent,Double diskPercent,Boolean posRunning,Boolean databaseRunning,String windowsVersion,String appVersion){}
 @PostMapping("/heartbeat") public Map<String,Object> heartbeat(@RequestHeader(value="X-Agent-Key",required=false) String key,@RequestBody Heartbeat h){
  if(key==null||!key.equals(props.getDevice().getAgentKey()))throw new IllegalArgumentException("Invalid device agent key");if(h.terminalId()==null||h.terminalId().isBlank())throw new IllegalArgumentException("terminalId is required");
  Device d=repo.findByTerminalId(h.terminalId()).orElseGet(Device::new);d.setTerminalId(h.terminalId());d.setDeviceName(h.deviceName()==null?h.terminalId():h.deviceName());d.setCpuPercent(h.cpuPercent());d.setRamPercent(h.ramPercent());d.setDiskPercent(h.diskPercent());d.setPosRunning(h.posRunning());d.setDatabaseRunning(h.databaseRunning());d.setWindowsVersion(h.windowsVersion());d.setAppVersion(h.appVersion());d.setLastSeen(LocalDateTime.now());repo.save(d);return Map.of("status","OK","serverTime",LocalDateTime.now());
 }
 @PreAuthorize("hasRole('ADMIN')") @GetMapping public List<Map<String,Object>> list(){return repo.findAll().stream().map(d->{Map<String,Object>m=new LinkedHashMap<>();m.put("id",d.getId());m.put("terminalId",d.getTerminalId());m.put("deviceName",d.getDeviceName());m.put("cpuPercent",d.getCpuPercent());m.put("ramPercent",d.getRamPercent());m.put("diskPercent",d.getDiskPercent());m.put("posRunning",d.getPosRunning());m.put("databaseRunning",d.getDatabaseRunning());m.put("windowsVersion",d.getWindowsVersion());m.put("appVersion",d.getAppVersion());m.put("lastSeen",d.getLastSeen());m.put("online",d.getLastSeen()!=null&&d.getLastSeen().isAfter(LocalDateTime.now().minusMinutes(3)));return m;}).toList();}
}
