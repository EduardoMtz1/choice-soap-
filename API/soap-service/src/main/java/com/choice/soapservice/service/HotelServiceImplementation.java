package com.choice.soapservice.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.choice.hotels.*;
import com.choice.soapservice.model.Amenity;
import com.choice.soapservice.model.Hotel;
import com.choice.soapservice.repository.HotelRepository;

@Service
@Transactional
public class HotelServiceImplementation implements HotelService{

    private HotelRepository hotelRepository;
    private AmenityService amenityService;
    private ErrorService errorService;

    public HotelServiceImplementation() {

    }

    @Autowired
    public HotelServiceImplementation(HotelRepository hotelRepository, AmenityService amenityService, ErrorService errorService) {
        this.hotelRepository = hotelRepository;
        this.amenityService = amenityService;
        this.errorService = errorService;
    }

    @Override
    public GetHotelByIdResponse findHotelById(GetHotelByIdRequest request) {
        GetHotelByIdResponse response = new GetHotelByIdResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        try{
            Hotel hotel = hotelRepository.findHotelById(request.getHotelId());
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
        } catch(Exception e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("An error ocurred on the server, please try again!");
            response.setServiceStatus(serviceStatus);
            return response;

        }
    }

    @Override
    public GetAllHotelsByNameResponse findAllByName(GetAllHotelsByNameRequest request) {
        GetAllHotelsByNameResponse response = new GetAllHotelsByNameResponse();
        ServiceStatus serviceStatus = this.errorService.checkPagination(request.getPageNumber(), request.getPageSize());

        if(!serviceStatus.getStatusCode().equals("CHECKED")) {
            response.setServiceStatus(serviceStatus);
            return response;
        }

        try {

            List<Hotel> savedHotels = hotelRepository.findAllByNameContainingIgnoreCase(request.getName(), PageRequest.of(request.getPageNumber(), request.getPageSize()));
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

        } catch(Exception e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("An error ocurred on the server, please try again!");
            response.setServiceStatus(serviceStatus);
            return response;

        }


    }

    @Override
    public CreateHotelResponse save(CreateHotelRequest request) {
        CreateHotelResponse response = new CreateHotelResponse();
        ServiceStatus serviceStatus = this.errorService.checkHotelInformation(request.getName(), request.getAddress(), request.getRating());

        if(!serviceStatus.getStatusCode().equals("CHECKED")) {
            response.setServiceStatus(serviceStatus);
            return response;
        }

        try{
            List<Hotel> hotelsByName = hotelRepository.findAllByName(request.getName());

            if(!hotelsByName.isEmpty()) {
                serviceStatus.setStatusCode("BAD_REQUEST");
                serviceStatus.setMessage("Hotel Already Exists!");
            } else {
                Hotel hotel = new Hotel();
                hotel.setName(request.getName());
                hotel.setAddress(request.getAddress());
                hotel.setRating(request.getRating());
                Hotel savedHotel = hotelRepository.save(hotel);

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
        } catch(Exception e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("An error ocurred on the server, please try again!");
            response.setServiceStatus(serviceStatus);
            return response;

        }
    }

    @Override
    public DeleteHotelResponse deleteById(DeleteHotelRequest request) {
        DeleteHotelResponse response = new DeleteHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        try{
            Hotel hotel = hotelRepository.findHotelById(request.getHotelId());
            if(hotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found!");
            } else {
                hotelRepository.deleteById(request.getHotelId());
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Hotel Deleted Succesfully!");
            }

            response.setServiceStatus(serviceStatus);
            return response;
        } catch(Exception e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("An error ocurred on the server, please try again!");
            response.setServiceStatus(serviceStatus);
            return response;

        }
    }

    @Override
    public UpdateHotelResponse updateHotel(UpdateHotelRequest request) {
        UpdateHotelResponse response = new UpdateHotelResponse();
        ServiceStatus serviceStatus = this.errorService.checkHotelInformation(request.getHotelInfo().getName(), request.getHotelInfo().getAddress(), request.getHotelInfo().getRating());

        if(!serviceStatus.getStatusCode().equals("CHECKED")) {
            response.setServiceStatus(serviceStatus);
            return response;
        }

        try{    
            Hotel savedHotel = hotelRepository.findHotelById(request.getHotelInfo().getHotelId());
            if(savedHotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found");

            } else {
                List<Hotel> hotelsByName = hotelRepository.findAllByName(request.getHotelInfo().getName());
                if(!hotelsByName.isEmpty()) {
                    serviceStatus.setStatusCode("BAD_REQUEST");
                    serviceStatus.setMessage("Updated Hotel Already Exists!");
                } else {
                    savedHotel.setName(request.getHotelInfo().getName());
                    savedHotel.setAddress(request.getHotelInfo().getAddress());
                    savedHotel.setRating(request.getHotelInfo().getRating());
                    hotelRepository.save(savedHotel);

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

        } catch(Exception e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("An error ocurred on the server, please try again!");
            response.setServiceStatus(serviceStatus);
            return response;

        }
    }

    @Override
    public AddAmenityResponse addAmenity(AddAmenityRequest request) {
        AddAmenityResponse response = new AddAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        try{
            Amenity amenity = amenityService.findAmenityById(request.getAmenityId());
            if(amenity == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Amenity Not Found!");
                response.setServiceStatus(serviceStatus);
                return response;
            } else {
                Hotel savedHotel = hotelRepository.findHotelById(request.getHotelId());
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
                    hotelRepository.save(savedHotel);
                    serviceStatus.setStatusCode("SUCCESS");
                    serviceStatus.setMessage("Amenity Added To Hotel!");
                }
            }
            response.setServiceStatus(serviceStatus);
            return response;
        } catch(Exception e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("An error ocurred on the server, please try again!");
            response.setServiceStatus(serviceStatus);
            return response;

        }
    }

    @Override
    public DeleteAmenityResponse deleteAmenity(DeleteAmenityRequest request) {
        DeleteAmenityResponse response = new DeleteAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        try{
            Hotel savedHotel = hotelRepository.findHotelById(request.getHotelId());
            if(savedHotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found!");
                response.setServiceStatus(serviceStatus);
                return response;
            }

            Amenity amenity = amenityService.findAmenityById(request.getAmenityId());
            if(amenity == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Amenity Not Found!");
                response.setServiceStatus(serviceStatus);
                return response;
            }

            for(Amenity hotelAmenity: savedHotel.getAmenities()) {
                if(hotelAmenity.getId() == amenity.getId()) {
                    savedHotel.getAmenities().remove(hotelAmenity);
                    hotelRepository.save(savedHotel);
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

        } catch(Exception e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("An error ocurred on the server, please try again!");
            response.setServiceStatus(serviceStatus);
            return response;

        }
    }
}
