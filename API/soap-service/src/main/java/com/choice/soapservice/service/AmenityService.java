package com.choice.soapservice.service;

import com.choice.soapservice.model.Amenity;

public interface AmenityService {
    
    public Amenity findAmenityById(long amenityId);
}
