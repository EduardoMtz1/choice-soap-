package com.choice.soapservice.service;

import java.util.List;

import com.choice.soapservice.model.Hotel;

public interface HotelService {

    public Hotel findHotelById(long hotelId);
    public List<Hotel> findAllByName(String name, int page, int size);
    public Hotel save(Hotel toSave);
    public void deleteById(long hotelId);
    public void updateHotel(Hotel toSave);
    public List<Hotel> findByName(String name);
}
