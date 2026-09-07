package com.example.travel.repository;

import com.example.travel.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Destination> findByNameIgnoreCase(String name);

    @Query("""
            SELECT d FROM Destination d
            WHERE (:search IS NULL OR
                LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(d.location) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(COALESCE(d.tags, '')) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:district IS NULL OR LOWER(d.district) = LOWER(:district))
              AND (:category IS NULL OR LOWER(d.category) = LOWER(:category))
              AND (:featured IS NULL OR d.featured = :featured)
            ORDER BY d.featured DESC, d.district ASC, d.name ASC
            """)
    List<Destination> search(
            @Param("search") String search,
            @Param("district") String district,
            @Param("category") String category,
            @Param("featured") Boolean featured
    );
}
