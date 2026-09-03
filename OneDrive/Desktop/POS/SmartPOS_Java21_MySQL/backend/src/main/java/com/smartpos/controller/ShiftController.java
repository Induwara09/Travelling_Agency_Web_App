package com.smartpos.controller;

import com.smartpos.dto.ShiftDtos.*;
import com.smartpos.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/shifts")
public class ShiftController {
    private final ShiftService service;public ShiftController(ShiftService s){service=s;}
    @PostMapping("/open") public ShiftView open(@Valid @RequestBody OpenShiftRequest r){return service.open(r);}
    @PostMapping("/close") public ShiftView close(@Valid @RequestBody CloseShiftRequest r){return service.close(r);}
    @GetMapping("/current") public ShiftView current(){return service.current();}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @GetMapping public List<ShiftView> all(){return service.all();}
}
