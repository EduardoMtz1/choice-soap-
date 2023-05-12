package com.choice.soapservice.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.choice.soapservice.model.Hotel;
import com.choice.soapservice.repository.HotelRepository;

@Service
@Transactional
public class HotelServiceImplementation implements HotelService{

    private HotelRepository hotelRepository;
    public HotelServiceImplementation() {

    }

    @Autowired
    public HotelServiceImplementation(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Hotel findHotelById(long hotelId) {
        return this.hotelRepository.findHotelById(hotelId);
    }

    @Override
    public List<Hotel> findAllByName(String name, int page, int size) {
        return this.hotelRepository.findAllByNameContainingIgnoreCase(name, PageRequest.of(page, size));
    }

    @Override
    public Hotel save(Hotel toSave) {
        return this.hotelRepository.save(toSave);
    }

    @Override
    public void deleteById(long hotelId) {
        this.hotelRepository.deleteById(hotelId);
    }

    @Override
    public void updateHotel(Hotel toSave) {
        this.hotelRepository.save(toSave);
    }
    
    @Override
    public List<Hotel> findByName(String name) {
        return this.hotelRepository.findAllByName(name);
    }
}
