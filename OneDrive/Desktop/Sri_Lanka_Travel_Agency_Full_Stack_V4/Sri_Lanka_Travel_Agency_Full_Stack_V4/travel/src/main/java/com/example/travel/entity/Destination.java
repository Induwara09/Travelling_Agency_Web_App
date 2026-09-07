package com.example.travel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "destinations")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Destination name is required")
    @Size(max = 100, message = "Destination name cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Destination location is required")
    @Size(max = 150, message = "Location cannot exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String location;

    @NotBlank(message = "District is required")
    @Size(max = 60, message = "District cannot exceed 60 characters")
    @Column(length = 60)
    private String district;

    @NotBlank(message = "Category is required")
    @Size(max = 60, message = "Category cannot exceed 60 characters")
    @Column(length = 60)
    private String category;

    @Size(max = 500, message = "Short description cannot exceed 500 characters")
    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Size(max = 500, message = "Image source URL cannot exceed 500 characters")
    @Column(name = "image_source_url", length = 500)
    private String imageSourceUrl;

    @Size(max = 20, message = "Image mode cannot exceed 20 characters")
    @Column(name = "image_mode", length = 20)
    private String imageMode = "AUTO";

    @Size(max = 300, message = "Tags cannot exceed 300 characters")
    @Column(length = 300)
    private String tags;

    @Column(nullable = false)
    private Boolean featured = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Destination() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (featured == null) featured = false;
        if (imageMode == null || imageMode.isBlank()) imageMode = "AUTO";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getImageSourceUrl() { return imageSourceUrl; }
    public void setImageSourceUrl(String imageSourceUrl) { this.imageSourceUrl = imageSourceUrl; }
    public String getImageMode() { return imageMode; }
    public void setImageMode(String imageMode) { this.imageMode = imageMode; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
