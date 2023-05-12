package com.choice.soapservice.repository;

import org.springframework.stereotype.Repository;

import com.choice.soapservice.model.Hotel;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface HotelRepository extends CrudRepository<Hotel, Long> {
    Hotel findHotelById(long hotelId);
    void deleteHotelById(long hotelId);
    List<Hotel> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Hotel> findAllByName(String name);
}