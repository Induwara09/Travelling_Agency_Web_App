package com.example.travel.service;

import com.example.travel.entity.Hotel;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.HotelRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Transactional
    public Hotel createHotel(Hotel hotel) {
        hotel.setId(null);
        if (hotel.getAvailable() == null) hotel.setAvailable(true);
        return hotelRepository.save(hotel);
    }

    @Transactional(readOnly = true)
    public List<Hotel> getHotels(String search, Boolean available) {
        Specification<Hotel> specification =
                (root, query, builder) -> builder.conjunction();

        if (StringUtils.hasText(search)) {
            String pattern = "%" + search.toLowerCase() + "%";
            specification = specification.and((root, query, builder) ->
                    builder.or(
                            builder.like(builder.lower(root.<String>get("name")), pattern),
                            builder.like(builder.lower(root.<String>get("location")), pattern)
                    )
            );
        }
        if (available != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("available"), available)
            );
        }

        return hotelRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: " + id
                ));
    }

    @Transactional
    public Hotel updateHotel(Long id, Hotel request) {
        Hotel hotel = getHotelById(id);
        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setDescription(request.getDescription());
        hotel.setPricePerNight(request.getPricePerNight());
        hotel.setRating(request.getRating());
        hotel.setImageUrl(request.getImageUrl());
        hotel.setAvailable(request.getAvailable() == null ? true : request.getAvailable());
        return hotelRepository.save(hotel);
    }

    @Transactional
    public void deleteHotel(Long id) {
        hotelRepository.delete(getHotelById(id));
    }
}
