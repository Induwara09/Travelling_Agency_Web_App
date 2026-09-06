package com.example.travel.config;

import com.example.travel.entity.Destination;
import com.example.travel.repository.DestinationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

@Component
@Order(20)
public class DestinationDataInitializer implements CommandLineRunner {

    private final DestinationRepository repository;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    public DestinationDataInitializer(
            DestinationRepository repository,
            ObjectMapper objectMapper,
            @Value("${app.destination-seed.enabled:true}") boolean enabled
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!enabled) return;

        ClassPathResource resource = new ClassPathResource("destinations-v3.json");
        List<Destination> catalogue;
        try (InputStream stream = resource.getInputStream()) {
            catalogue = objectMapper.readValue(stream, new TypeReference<>() {});
        }

        List<Destination> newDestinations = new ArrayList<>();
        for (Destination destination : catalogue) {
            repository.findByNameIgnoreCase(destination.getName()).ifPresentOrElse(existing -> {
                if (!StringUtils.hasText(existing.getDistrict()) || "Sri Lanka".equals(existing.getDistrict())) {
                    existing.setDistrict(destination.getDistrict());
                    existing.setCategory(destination.getCategory());
                    existing.setShortDescription(destination.getShortDescription());
                    existing.setImageSourceUrl(destination.getImageSourceUrl());
                    existing.setTags(destination.getTags());
                    existing.setFeatured(destination.getFeatured());
                    if (!StringUtils.hasText(existing.getDescription())) existing.setDescription(destination.getDescription());
                    if (!StringUtils.hasText(existing.getImageUrl())) existing.setImageUrl(destination.getImageUrl());
                    repository.save(existing);
                }
            }, () -> {
                destination.setId(null);
                newDestinations.add(destination);
            });
        }
        if (!newDestinations.isEmpty()) repository.saveAll(newDestinations);
    }
}
