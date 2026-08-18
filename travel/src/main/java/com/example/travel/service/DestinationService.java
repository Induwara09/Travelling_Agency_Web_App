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
        return destinationRepository.save(destination);
    }

    @Transactional(readOnly = true)
    public List<Destination> getAllDestinations(String search) {
        if (!StringUtils.hasText(search)) {
            return destinationRepository.findAll();
        }
        return destinationRepository
                .findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(search, search);
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
        destination.setDescription(request.getDescription());
        destination.setImageUrl(request.getImageUrl());
        return destinationRepository.save(destination);
    }

    @Transactional
    public void deleteDestination(Long id) {
        destinationRepository.delete(getDestinationById(id));
    }
}
