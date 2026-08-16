package com.example.travel.repository;

import com.example.travel.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    List<Destination> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String name,
            String location
    );
}
