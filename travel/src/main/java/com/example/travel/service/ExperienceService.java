package com.example.travel.service;

import com.example.travel.entity.Experience;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.ExperienceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    @Transactional(readOnly = true)
    public List<Experience> getExperiences(String search, String category, Boolean active, Boolean featured) {
        Specification<Experience> specification = (root, query, builder) -> builder.conjunction();

        if (StringUtils.hasText(search)) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            specification = specification.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.<String>get("title")), pattern),
                    builder.like(builder.lower(root.<String>get("location")), pattern),
                    builder.like(builder.lower(root.<String>get("category")), pattern)
            ));
        }
        if (StringUtils.hasText(category)) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(builder.lower(root.<String>get("category")), category.trim().toLowerCase()));
        }
        if (active != null) {
            specification = specification.and((root, query, builder) -> builder.equal(root.get("active"), active));
        }
        if (featured != null) {
            specification = specification.and((root, query, builder) -> builder.equal(root.get("featured"), featured));
        }

        return experienceRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional(readOnly = true)
    public Experience getExperience(Long id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + id));
    }

    @Transactional
    public Experience createExperience(Experience experience) {
        experience.setId(null);
        if (experience.getActive() == null) experience.setActive(true);
        if (experience.getFeatured() == null) experience.setFeatured(false);
        return experienceRepository.save(experience);
    }

    @Transactional
    public Experience updateExperience(Long id, Experience request) {
        Experience experience = getExperience(id);
        experience.setTitle(request.getTitle());
        experience.setCategory(request.getCategory());
        experience.setLocation(request.getLocation());
        experience.setShortDescription(request.getShortDescription());
        experience.setDescription(request.getDescription());
        experience.setDurationHours(request.getDurationHours());
        experience.setImageUrl(request.getImageUrl());
        experience.setFeatured(request.getFeatured() == null ? false : request.getFeatured());
        experience.setActive(request.getActive() == null ? true : request.getActive());
        return experienceRepository.save(experience);
    }

    @Transactional
    public void deleteExperience(Long id) {
        experienceRepository.delete(getExperience(id));
    }
}
