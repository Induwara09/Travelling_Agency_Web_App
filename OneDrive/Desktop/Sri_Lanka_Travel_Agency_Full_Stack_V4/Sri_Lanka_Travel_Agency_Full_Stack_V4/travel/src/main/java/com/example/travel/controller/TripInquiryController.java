package com.example.travel.controller;

import com.example.travel.dto.inquiry.InquiryStatusUpdateRequest;
import com.example.travel.dto.inquiry.TripInquiryRequest;
import com.example.travel.entity.TripInquiry;
import com.example.travel.service.TripInquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TripInquiryController {

    private final TripInquiryService inquiryService;

    public TripInquiryController(TripInquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping("/inquiries")
    public ResponseEntity<TripInquiry> create(@Valid @RequestBody TripInquiryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.create(request));
    }

    @GetMapping("/admin/inquiries")
    public ResponseEntity<List<TripInquiry>> list() {
        return ResponseEntity.ok(inquiryService.getAll());
    }

    @PutMapping("/admin/inquiries/{id}/status")
    public ResponseEntity<TripInquiry> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody InquiryStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(inquiryService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/admin/inquiries/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inquiryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
