package com.choice.soapservice.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import com.choice.soapservice.model.Amenity;

@Repository
public interface AmenityRepository extends CrudRepository<Amenity, Long>{
    Amenity findAmenityById(long amenityId);
}
