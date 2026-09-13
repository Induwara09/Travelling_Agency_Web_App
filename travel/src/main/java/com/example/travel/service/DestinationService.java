package com.example.travel.service;

import com.example.travel.entity.Destination;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.DestinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Transactional
    public Destination createDestination(Destination destination) {
        destination.setId(null);
        if (destination.getFeatured() == null) destination.setFeatured(false);
        destination.setImageMode(StringUtils.hasText(destination.getImageUrl()) ? "CUSTOM" : "AUTO");
        return destinationRepository.save(destination);
    }

    @Transactional(readOnly = true)
    public List<Destination> getAllDestinations(
            String search,
            String district,
            String category,
            Boolean featured
    ) {
        return destinationRepository.search(
                clean(search), clean(district), clean(category), featured
        );
    }

    @Transactional(readOnly = true)
    public Destination getDestinationById(Long id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destination not found with id: " + id
                ));
    }

    @Transactional
    public Destination updateDestination(Long id, Destination request) {
        Destination destination = getDestinationById(id);
        destination.setName(request.getName());
        destination.setLocation(request.getLocation());
        destination.setDistrict(request.getDistrict());
        destination.setCategory(request.getCategory());
        destination.setShortDescription(request.getShortDescription());
        destination.setDescription(request.getDescription());
        destination.setImageUrl(request.getImageUrl());
        destination.setImageSourceUrl(request.getImageSourceUrl());
        destination.setImageMode(StringUtils.hasText(request.getImageUrl()) ? "CUSTOM" : "AUTO");
        destination.setTags(request.getTags());
        destination.setFeatured(Boolean.TRUE.equals(request.getFeatured()));
        return destinationRepository.save(destination);
    }

    @Transactional
    public void deleteDestination(Long id) {
        destinationRepository.delete(getDestinationById(id));
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
