package com.choice.soapservice.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choice.soapservice.model.Amenity;
import com.choice.soapservice.repository.AmenityRepository;

@Service
@Transactional
public class AmenityServiceImplementation implements AmenityService {
    private AmenityRepository amenityRepository;

    public AmenityServiceImplementation(){

    }

    @Autowired
    public AmenityServiceImplementation(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    @Override
    public Amenity findAmenityById(long amenityId) {
        return this.amenityRepository.findAmenityById(amenityId);
    }

    
}
