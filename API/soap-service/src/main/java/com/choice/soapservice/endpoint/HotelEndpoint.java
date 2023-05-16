package com.choice.soapservice.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.choice.soapservice.service.HotelService;

import com.choice.hotels.GetHotelByIdResponse;
import com.choice.hotels.AddAmenityRequest;
import com.choice.hotels.AddAmenityResponse;
import com.choice.hotels.CreateHotelRequest;
import com.choice.hotels.CreateHotelResponse;
import com.choice.hotels.DeleteAmenityRequest;
import com.choice.hotels.DeleteAmenityResponse;
import com.choice.hotels.DeleteHotelRequest;
import com.choice.hotels.DeleteHotelResponse;
import com.choice.hotels.GetAllHotelsByNameRequest;
import com.choice.hotels.GetAllHotelsByNameResponse;
import com.choice.hotels.GetHotelByIdRequest;
import com.choice.hotels.UpdateHotelRequest;
import com.choice.hotels.UpdateHotelResponse;

@Endpoint
public class HotelEndpoint {
    public static final String URI = "http://localhost:9090";

    private HotelService hotelService;


    public HotelEndpoint() {

    }

    @Autowired
    public HotelEndpoint(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PayloadRoot( namespace = URI, localPart = "getHotelByIdRequest")
    @ResponsePayload
    public GetHotelByIdResponse getHotelByIdRequest(@RequestPayload GetHotelByIdRequest request) {
        return hotelService.findHotelById(request);
    }

    @PayloadRoot( namespace = URI, localPart = "createHotelRequest")
    @ResponsePayload
    public CreateHotelResponse createHotelRequest(@RequestPayload CreateHotelRequest request) {
        return hotelService.save(request);
    }

    @PayloadRoot( namespace = URI, localPart = "updateHotelRequest")
    @ResponsePayload
    public UpdateHotelResponse updateHotelRequest(@RequestPayload UpdateHotelRequest request) {
        return hotelService.updateHotel(request);
    }

    @PayloadRoot( namespace = URI, localPart = "deleteHotelRequest")
    @ResponsePayload
    public DeleteHotelResponse deleteHotelRequest(@RequestPayload DeleteHotelRequest request) {
        return hotelService.deleteById(request);
    }

    @PayloadRoot( namespace = URI, localPart = "addAmenityRequest")
    @ResponsePayload
    public AddAmenityResponse addAmenityRequest(@RequestPayload AddAmenityRequest request) {
        return hotelService.addAmenity(request);
    }

    @PayloadRoot( namespace = URI, localPart = "deleteAmenityRequest")
    @ResponsePayload
    public DeleteAmenityResponse deleteAmenityRequest(@RequestPayload DeleteAmenityRequest request) {
        return hotelService.deleteAmenity(request);
    }

    @PayloadRoot(namespace = URI, localPart = "getAllHotelsByNameRequest")
    @ResponsePayload
    public GetAllHotelsByNameResponse getAllHotelsByNameRequest(@RequestPayload GetAllHotelsByNameRequest request) {
        return hotelService.findAllByName(request);
    }

}
