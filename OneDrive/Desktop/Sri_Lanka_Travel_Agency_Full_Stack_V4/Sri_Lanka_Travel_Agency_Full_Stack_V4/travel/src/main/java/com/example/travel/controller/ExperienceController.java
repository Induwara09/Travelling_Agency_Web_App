package com.example.travel.controller;

import com.example.travel.entity.Experience;
import com.example.travel.service.ExperienceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public ResponseEntity<List<Experience>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean featured
    ) {
        return ResponseEntity.ok(experienceService.getExperiences(search, category, active, featured));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Experience> get(@PathVariable Long id) {
        return ResponseEntity.ok(experienceService.getExperience(id));
    }

    @PostMapping
    public ResponseEntity<Experience> create(@Valid @RequestBody Experience experience) {
        return ResponseEntity.status(HttpStatus.CREATED).body(experienceService.createExperience(experience));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Experience> update(@PathVariable Long id, @Valid @RequestBody Experience experience) {
        return ResponseEntity.ok(experienceService.updateExperience(id, experience));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        experienceService.deleteExperience(id);
        return ResponseEntity.noContent().build();
    }
}
