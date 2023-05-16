package com.choice.soapservice.service;

import com.choice.hotels.*;

public interface HotelService {

    public GetHotelByIdResponse findHotelById(GetHotelByIdRequest request);
    public GetAllHotelsByNameResponse findAllByName(GetAllHotelsByNameRequest request);
    public CreateHotelResponse save(CreateHotelRequest request);
    public DeleteHotelResponse deleteById(DeleteHotelRequest request);
    public UpdateHotelResponse updateHotel(UpdateHotelRequest request);
    public AddAmenityResponse addAmenity(AddAmenityRequest request);
    public DeleteAmenityResponse deleteAmenity(DeleteAmenityRequest request);
}
