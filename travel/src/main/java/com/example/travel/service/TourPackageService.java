package com.example.travel.service;

import com.example.travel.dto.packageinfo.TourPackageRequest;
import com.example.travel.dto.packageinfo.TourPackageResponse;
import com.example.travel.entity.Destination;
import com.example.travel.entity.TourPackage;
import com.example.travel.enums.PackageStatus;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.DestinationRepository;
import com.example.travel.repository.TourPackageRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;
    private final DestinationRepository destinationRepository;

    public TourPackageService(
            TourPackageRepository tourPackageRepository,
            DestinationRepository destinationRepository
    ) {
        this.tourPackageRepository = tourPackageRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional
    public TourPackageResponse createPackage(TourPackageRequest request) {
        TourPackage tourPackage = new TourPackage();
        applyRequest(tourPackage, request);
        return toResponse(tourPackageRepository.save(tourPackage));
    }

    @Transactional(readOnly = true)
    public List<TourPackageResponse> getPackages(
            String search,
            String category,
            Long destinationId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minDuration,
            Integer maxDuration,
            PackageStatus status
    ) {
        Specification<TourPackage> specification =
                (root, query, builder) -> builder.conjunction();

        if (StringUtils.hasText(search)) {
            String pattern = "%" + search.toLowerCase() + "%";
            specification = specification.and((root, query, builder) ->
                    builder.like(builder.lower(root.<String>get("name")), pattern)
            );
        }
        if (StringUtils.hasText(category)) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(
                            builder.lower(root.<String>get("category")),
                            category.toLowerCase()
                    )
            );
        }
        if (destinationId != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("destination").get("id"), destinationId)
            );
        }
        if (minPrice != null) {
            specification = specification.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("price"), minPrice)
            );
        }
        if (maxPrice != null) {
            specification = specification.and((root, query, builder) ->
                    builder.lessThanOrEqualTo(root.get("price"), maxPrice)
            );
        }
        if (minDuration != null) {
            specification = specification.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("durationDays"), minDuration)
            );
        }
        if (maxDuration != null) {
            specification = specification.and((root, query, builder) ->
                    builder.lessThanOrEqualTo(root.get("durationDays"), maxDuration)
            );
        }
        if (status != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("status"), status)
            );
        }

        return tourPackageRepository.findAll(specification)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TourPackageResponse getPackageById(Long id) {
        return toResponse(findPackage(id));
    }

    @Transactional
    public TourPackageResponse updatePackage(Long id, TourPackageRequest request) {
        TourPackage tourPackage = findPackage(id);
        applyRequest(tourPackage, request);
        return toResponse(tourPackageRepository.save(tourPackage));
    }

    @Transactional
    public void deletePackage(Long id) {
        tourPackageRepository.delete(findPackage(id));
    }

    @Transactional(readOnly = true)
    public TourPackage findPackage(Long id) {
        return tourPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tour package not found with id: " + id
                ));
    }

    private void applyRequest(TourPackage tourPackage, TourPackageRequest request) {
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destination not found with id: " + request.destinationId()
                ));

        tourPackage.setName(request.name().trim());
        tourPackage.setDescription(request.description());
        tourPackage.setPrice(request.price());
        tourPackage.setDurationDays(request.durationDays());
        tourPackage.setImageUrl(request.imageUrl());
        tourPackage.setCategory(request.category().trim());
        tourPackage.setStatus(
                request.status() == null ? PackageStatus.ACTIVE : request.status()
        );
        tourPackage.setDestination(destination);
    }

    private TourPackageResponse toResponse(TourPackage tourPackage) {
        Destination destination = tourPackage.getDestination();
        return new TourPackageResponse(
                tourPackage.getId(),
                tourPackage.getName(),
                tourPackage.getDescription(),
                tourPackage.getPrice(),
                tourPackage.getDurationDays(),
                tourPackage.getImageUrl(),
                tourPackage.getCategory(),
                tourPackage.getStatus(),
                destination.getId(),
                destination.getName(),
                tourPackage.getCreatedAt()
        );
    }
}
