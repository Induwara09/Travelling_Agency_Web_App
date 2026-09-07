package com.example.travel.controller;

import com.example.travel.dto.packageinfo.TourPackageRequest;
import com.example.travel.dto.packageinfo.TourPackageResponse;
import com.example.travel.enums.PackageStatus;
import com.example.travel.service.TourPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class TourPackageController {

    private final TourPackageService tourPackageService;

    public TourPackageController(TourPackageService tourPackageService) {
        this.tourPackageService = tourPackageService;
    }

    @PostMapping
    public ResponseEntity<TourPackageResponse> createPackage(
            @Valid @RequestBody TourPackageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tourPackageService.createPackage(request));
    }

    @GetMapping
    public ResponseEntity<List<TourPackageResponse>> getPackages(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) PackageStatus status
    ) {
        return ResponseEntity.ok(
                tourPackageService.getPackages(
                        search,
                        category,
                        destinationId,
                        minPrice,
                        maxPrice,
                        minDuration,
                        maxDuration,
                        status
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPackageResponse> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(tourPackageService.getPackageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourPackageResponse> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody TourPackageRequest request
    ) {
        return ResponseEntity.ok(tourPackageService.updatePackage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        tourPackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
