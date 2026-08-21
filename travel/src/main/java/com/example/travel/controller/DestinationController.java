package com.example.travel.controller;

import com.example.travel.entity.Destination;
import com.example.travel.service.DestinationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @PostMapping
    public ResponseEntity<Destination> createDestination(
            @Valid @RequestBody Destination destination
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(destinationService.createDestination(destination));
    }

    @GetMapping
    public ResponseEntity<List<Destination>> getAllDestinations(
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(destinationService.getAllDestinations(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Destination> getDestinationById(@PathVariable Long id) {
        return ResponseEntity.ok(destinationService.getDestinationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Destination> updateDestination(
            @PathVariable Long id,
            @Valid @RequestBody Destination destination
    ) {
        return ResponseEntity.ok(
                destinationService.updateDestination(id, destination)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
        destinationService.deleteDestination(id);
        return ResponseEntity.noContent().build();
    }
}
