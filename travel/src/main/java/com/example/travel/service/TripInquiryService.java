package com.example.travel.service;

import com.example.travel.dto.inquiry.TripInquiryRequest;
import com.example.travel.entity.TripInquiry;
import com.example.travel.enums.InquiryStatus;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.TripInquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TripInquiryService {

    private final TripInquiryRepository inquiryRepository;

    public TripInquiryService(TripInquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @Transactional
    public TripInquiry create(TripInquiryRequest request) {
        TripInquiry inquiry = new TripInquiry();
        inquiry.setName(request.name().trim());
        inquiry.setEmail(request.email().trim().toLowerCase());
        inquiry.setPhone(clean(request.phone()));
        inquiry.setCountry(clean(request.country()));
        inquiry.setTravelMonth(clean(request.travelMonth()));
        inquiry.setGuests(request.guests());
        inquiry.setInterests(clean(request.interests()));
        inquiry.setMessage(request.message().trim());
        inquiry.setStatus(InquiryStatus.NEW);
        return inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<TripInquiry> getAll() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public TripInquiry updateStatus(Long id, InquiryStatus status) {
        TripInquiry inquiry = get(id);
        inquiry.setStatus(status);
        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public void delete(Long id) {
        inquiryRepository.delete(get(id));
    }

    private TripInquiry get(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip inquiry not found with id: " + id));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
