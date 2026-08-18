package com.example.travel.repository;

import com.example.travel.entity.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TourPackageRepository extends
        JpaRepository<TourPackage, Long>,
        JpaSpecificationExecutor<TourPackage> {
}
