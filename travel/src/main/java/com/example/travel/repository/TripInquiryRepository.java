package com.example.travel.repository;

import com.example.travel.entity.TripInquiry;
import com.example.travel.enums.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripInquiryRepository extends JpaRepository<TripInquiry, Long> {
    long countByStatus(InquiryStatus status);
    List<TripInquiry> findAllByOrderByCreatedAtDesc();
}
