package com.choice.soapservice.endpoint;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.choice.soapservice.model.Amenity;
import com.choice.soapservice.model.Hotel;
import com.choice.soapservice.service.AmenityService;
import com.choice.soapservice.service.HotelService;

import com.choice.hotels.GetHotelByIdResponse;
import com.choice.hotels.AddAmenityRequest;
import com.choice.hotels.AddAmenityResponse;
import com.choice.hotels.AmenityInfo;
import com.choice.hotels.CreateHotelRequest;
import com.choice.hotels.CreateHotelResponse;
import com.choice.hotels.DeleteAmenityRequest;
import com.choice.hotels.DeleteAmenityResponse;
import com.choice.hotels.DeleteHotelRequest;
import com.choice.hotels.DeleteHotelResponse;
import com.choice.hotels.GetAllHotelsByNameRequest;
import com.choice.hotels.GetAllHotelsByNameResponse;
import com.choice.hotels.GetHotelByIdRequest;
import com.choice.hotels.HotelInfo;
import com.choice.hotels.ServiceStatus;
import com.choice.hotels.UpdateHotelRequest;
import com.choice.hotels.UpdateHotelResponse;

@Endpoint
public class HotelEndpoint {
    public static final String URI = "http://localhost:9090";

    private HotelService hotelService;
    private AmenityService amenityService;

    public HotelEndpoint() {

    }

    @Autowired
    public HotelEndpoint(HotelService hotelService, AmenityService amenityService) {
        this.hotelService = hotelService;
        this.amenityService = amenityService;
    }

    @PayloadRoot( namespace = URI, localPart = "getHotelByIdRequest")
    @ResponsePayload
    public GetHotelByIdResponse getHotelByIdRequest(@RequestPayload GetHotelByIdRequest request) {
        GetHotelByIdResponse response = new GetHotelByIdResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        Hotel hotel = hotelService.findHotelById(request.getHotelId());
        if(hotel == null) {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage("Hotel not found");
        } else {
            HotelInfo hotelInfo = new HotelInfo();
            hotelInfo.setHotelId(hotel.getId());
            hotelInfo.setName(hotel.getName());
            hotelInfo.setAddress(hotel.getAddress());
            hotelInfo.setRating(hotel.getRating());

            for(Amenity amenity: hotel.getAmenities()) {
                    AmenityInfo amenityInfo = new AmenityInfo();
                    amenityInfo.setAmenityId(amenity.getId());
                    amenityInfo.setName(amenity.getName());
                    hotelInfo.getAmenities().add(amenityInfo);
                }
            response.setHotelInfo(hotelInfo);

            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Hotel found!");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot( namespace = URI, localPart = "createHotelRequest")
    @ResponsePayload
    public CreateHotelResponse createHotelRequest(@RequestPayload CreateHotelRequest request) {
        CreateHotelResponse response = new CreateHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        List<Hotel> hotelsByName = this.hotelService.findByName(request.getName());

        if(!hotelsByName.isEmpty()) {
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Hotel Already Exists!");
        } else {
            Hotel hotel = new Hotel();
            hotel.setName(request.getName());
            hotel.setAddress(request.getAddress());
            hotel.setRating(request.getRating());
            Hotel savedHotel = hotelService.save(hotel);

            HotelInfo hotelInfo = new HotelInfo();
            hotelInfo.setHotelId(savedHotel.getId());
            hotelInfo.setName(savedHotel.getName());
            hotelInfo.setAddress(savedHotel.getAddress());
            hotelInfo.setRating(savedHotel.getRating());
            response.setHotelInfo(hotelInfo);
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Hotel saved succesfully!");
        }
        
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot( namespace = URI, localPart = "updateHotelRequest")
    @ResponsePayload
    public UpdateHotelResponse updateHotelRequest(@RequestPayload UpdateHotelRequest request) {
        UpdateHotelResponse response = new UpdateHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        Hotel savedHotel = this.hotelService.findHotelById(request.getHotelInfo().getHotelId());
        if(savedHotel == null) {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage("Hotel Not Found");
            
        } else {
            List<Hotel> hotelsByName = this.hotelService.findByName(request.getHotelInfo().getName());
            if(!hotelsByName.isEmpty()) {
                serviceStatus.setStatusCode("BAD_REQUEST");
                serviceStatus.setMessage("Updated Hotel Already Exists!");
            } else {
                savedHotel.setName(request.getHotelInfo().getName());
                savedHotel.setAddress(request.getHotelInfo().getAddress());
                savedHotel.setRating(request.getHotelInfo().getRating());
                this.hotelService.save(savedHotel);

                HotelInfo hotelInfo = new HotelInfo();
                hotelInfo.setHotelId(savedHotel.getId());
                hotelInfo.setName(savedHotel.getName());
                hotelInfo.setAddress(savedHotel.getAddress());
                hotelInfo.setRating(savedHotel.getRating());
                

                for(Amenity amenity: savedHotel.getAmenities()) {
                    AmenityInfo amenityInfo = new AmenityInfo();
                    amenityInfo.setAmenityId(amenity.getId());
                    amenityInfo.setName(amenity.getName());
                    hotelInfo.getAmenities().add(amenityInfo);
                }

                response.setHotelInfo(hotelInfo);
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Hotel Information Updated!");
            }
        }

        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot( namespace = URI, localPart = "deleteHotelRequest")
    @ResponsePayload
    public DeleteHotelResponse deleteHotelRequest(@RequestPayload DeleteHotelRequest request) {
        DeleteHotelResponse response = new DeleteHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        Hotel hotel = this.hotelService.findHotelById(request.getHotelId());
        if(hotel == null) {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage("Hotel Not Found!");
        } else {
            this.hotelService.deleteById(request.getHotelId());
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Hotel Deleted Succesfully!");
        }

        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot( namespace = URI, localPart = "addAmenityRequest")
    @ResponsePayload
    public AddAmenityResponse addAmenityRequest(@RequestPayload AddAmenityRequest request) {
        AddAmenityResponse response = new AddAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        Amenity amenity = this.amenityService.findAmenityById(request.getAmenityId());
        if(amenity == null) {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage("Amenity Not Found!");
            response.setServiceStatus(serviceStatus);
            return response;
        } else {
            Hotel savedHotel = this.hotelService.findHotelById(request.getHotelId());
            if(savedHotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found!");
                response.setServiceStatus(serviceStatus);
                return response;
            } else {
                for(Amenity hotelAmenity: savedHotel.getAmenities()) {
                    if(hotelAmenity.getId() == request.getAmenityId()) {
                        serviceStatus.setStatusCode("BAD_REQUEST");
                        serviceStatus.setMessage("Amenity Already Exist In Hotel!");
                        response.setServiceStatus(serviceStatus);
                        return response;
                    }
                }
                savedHotel.getAmenities().add(amenity);
                this.hotelService.save(savedHotel);
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Amenity Added To Hotel!");
            }
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot( namespace = URI, localPart = "deleteAmenityRequest")
    @ResponsePayload
    public DeleteAmenityResponse deleteAmenityRequest(@RequestPayload DeleteAmenityRequest request) {
        DeleteAmenityResponse response = new DeleteAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        Hotel savedHotel = this.hotelService.findHotelById(request.getHotelId());
        if(savedHotel == null) {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage("Hotel Not Found!");
            response.setServiceStatus(serviceStatus);
            return response;
        }

        Amenity amenity = this.amenityService.findAmenityById(request.getAmenityId());
        if(amenity == null) {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage("Amenity Not Found!");
            response.setServiceStatus(serviceStatus);
            return response;
        }

        for(Amenity hotelAmenity: savedHotel.getAmenities()) {
            if(hotelAmenity.getId() == amenity.getId()) {
                savedHotel.getAmenities().remove(hotelAmenity);
                this.hotelService.save(savedHotel);
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Amenity Deleted From Hotel!");
                response.setServiceStatus(serviceStatus);
                return response;
            }
        }

        serviceStatus.setStatusCode("BAD_REQUEST");
        serviceStatus.setMessage("Amenity Not Found In Hotel!");
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot(namespace = URI, localPart = "getAllHotelsByNameRequest")
    @ResponsePayload
    public GetAllHotelsByNameResponse getAllHotelsByNameRequest(@RequestPayload GetAllHotelsByNameRequest request) {
        GetAllHotelsByNameResponse response = new GetAllHotelsByNameResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        List<Hotel> savedHotels = this.hotelService.findAllByName(request.getName(), request.getPageNumber(), request.getPageSize());
        if(savedHotels.isEmpty()) {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage("No Hotels Found By Name!");
            response.setServiceStatus(serviceStatus);
            return response;
        }

        for(Hotel hotel: savedHotels){
            HotelInfo hotelInfo = new HotelInfo();
            hotelInfo.setHotelId(hotel.getId());
            hotelInfo.setName(hotel.getName());
            hotelInfo.setAddress(hotel.getAddress());
            hotelInfo.setRating(hotel.getRating());
                
            for(Amenity amenity: hotel.getAmenities()) {
                AmenityInfo amenityInfo = new AmenityInfo();
                amenityInfo.setAmenityId(amenity.getId());
                amenityInfo.setName(amenity.getName());
                hotelInfo.getAmenities().add(amenityInfo);
            }

            response.getHotelInfo().add(hotelInfo);
        }
        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Hotels Found By Name!");
        response.setServiceStatus(serviceStatus);
        return response;
    }

}
