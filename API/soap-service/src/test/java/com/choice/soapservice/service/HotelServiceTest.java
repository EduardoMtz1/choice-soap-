package com.choice.soapservice.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import com.choice.hotels.GetHotelByIdResponse;
import com.choice.hotels.HotelInfo;
import com.choice.hotels.ServiceStatus;
import com.choice.hotels.UpdateHotelRequest;
import com.choice.hotels.UpdateHotelResponse;
import com.choice.soapservice.model.Amenity;
import com.choice.soapservice.model.Hotel;
import com.choice.soapservice.repository.HotelRepository;

@SpringBootTest
public class HotelServiceTest {
    @InjectMocks
    private HotelServiceImplementation hotelService;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private AmenityService amenityService;
    @Mock
    private ErrorService errorService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGetNotFoundByFindHotelById() {
        GetHotelByIdRequest request = new GetHotelByIdRequest();
        request.setHotelId(10L);

        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(null);
        GetHotelByIdResponse savedHotel = hotelService.findHotelById(request);

        assertEquals("NOT_FOUND", savedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel not found", savedHotel.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetHotelByFindHotelById() {
        GetHotelByIdRequest request = new GetHotelByIdRequest();
        request.setHotelId(1L);

        Hotel hotel = new Hotel(1L, "Holiday Inn Express", "Carr. Guadalajara - Chapala 7012", 4);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WIFI");
        hotel.getAmenities().add(amenity);

        when(hotelRepository.findHotelById(1L)).thenReturn(hotel);
        GetHotelByIdResponse savedHotel = hotelService.findHotelById(request);

        assertEquals("SUCCESS", savedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel found!", savedHotel.getServiceStatus().getMessage());
        assertEquals(1L, savedHotel.getHotelInfo().getHotelId());
        assertEquals("Holiday Inn Express", savedHotel.getHotelInfo().getName());
        assertEquals("Carr. Guadalajara - Chapala 7012",savedHotel.getHotelInfo().getAddress());
        assertEquals(1L, savedHotel.getHotelInfo().getAmenities().get(0).getAmenityId());
        assertEquals("WIFI", savedHotel.getHotelInfo().getAmenities().get(0).getName());
    }

    @Test
    public void shouldFindAllHotelsByName() {
        GetAllHotelsByNameRequest request = new GetAllHotelsByNameRequest();
        request.setName("Fiesta");
        request.setPageNumber(0);
        request.setPageSize(2);

        List<Hotel> hotelList = new ArrayList<>();
        Hotel hotel1 = new Hotel(1L, "Fiesta Inn - Las Americas", "Av. Adolfo Lopez Mateos 30", 4);
        Hotel hotel2 = new Hotel(2L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);

        Set<Amenity> amenities = new HashSet<>();
        hotel1.setAmenities(amenities);
        hotel2.setAmenities(amenities);

        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WIFI");
        amenities.add(amenity);
        hotel1.getAmenities().add(amenity);

        hotelList.add(hotel1);
        hotelList.add(hotel2);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkPagination(request.getPageNumber(), request.getPageSize())).thenReturn(serviceStatus);
        Pageable page = PageRequest.of(request.getPageNumber(), request.getPageSize());
        when(hotelRepository.findAllByNameContainingIgnoreCase(request.getName(), page)).thenReturn(hotelList);
        GetAllHotelsByNameResponse savedHotels = hotelService.findAllByName(request);

        assertEquals(1L, savedHotels.getHotelInfo().get(0).getHotelId());
        assertEquals("Fiesta Inn - Las Americas", savedHotels.getHotelInfo().get(0).getName());
        assertEquals("Av. Adolfo Lopez Mateos 30", savedHotels.getHotelInfo().get(0).getAddress());
        assertEquals(4, savedHotels.getHotelInfo().get(0).getRating());
        assertEquals(1L, savedHotels.getHotelInfo().get(0).getAmenities().get(0).getAmenityId());
        assertEquals("WIFI", savedHotels.getHotelInfo().get(0).getAmenities().get(0).getName());

        assertEquals(2L, savedHotels.getHotelInfo().get(1).getHotelId());
        assertEquals("Fiesta Americana - Las Americas", savedHotels.getHotelInfo().get(1).getName());
        assertEquals("Av. Adolfo Lopez Mateos 40", savedHotels.getHotelInfo().get(1).getAddress());
        assertEquals(3, savedHotels.getHotelInfo().get(1).getRating());
    }

    @Test
    public void shouldGetBadRequesWhenCheckHotelByCreateHotel() {
        CreateHotelRequest request = new CreateHotelRequest();
        request.setName("");
        request.setAddress("");
        request.setRating(0);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("BAD_REQUEST");

        when(errorService.checkHotelInformation(request.getName(), request.getAddress(), request.getRating())).thenReturn(serviceStatus);
        CreateHotelResponse savedHotel = hotelService.save(request);
        
        assertEquals("BAD_REQUEST", savedHotel.getServiceStatus().getStatusCode());
    
    }

    @Test
    public void shouldGetBadRequestWhenHotelAlreadySavedCreateAddHotel() {
        CreateHotelRequest request = new CreateHotelRequest();
        request.setName("Fiesta Americana - Las Americas");
        request.setAddress("Av. Adolfo Lopez Mateos 40");
        request.setRating(3);

        List<Hotel> savedHotels = new ArrayList<>();
        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);
        savedHotels.add(hotel);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotelInformation(request.getName(), request.getAddress(), request.getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findAllByName(request.getName())).thenReturn(savedHotels);

        CreateHotelResponse savedHotel = hotelService.save(request);

        assertEquals("BAD_REQUEST", savedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel Already Exists!", savedHotel.getServiceStatus().getMessage());
        
    }

    @Test
    public void shouldCreateHotel() {
        CreateHotelRequest request = new CreateHotelRequest();
        request.setName("Fiesta Americana - Las Americas");
        request.setAddress("Av. Adolfo Lopez Mateos 40");
        request.setRating(3);

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);

        List<Hotel> savedHotels = new ArrayList<>();

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotelInformation(request.getName(), request.getAddress(), request.getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findAllByName(request.getName())).thenReturn(savedHotels);
        when(hotelRepository.save(Mockito.any())).thenReturn(hotel);

        CreateHotelResponse savedHotel = hotelService.save(request);

        assertEquals("SUCCESS", savedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel saved succesfully!",savedHotel.getServiceStatus().getMessage());
        assertEquals(1L, savedHotel.getHotelInfo().getHotelId());
        assertEquals("Fiesta Americana - Las Americas", savedHotel.getHotelInfo().getName());
        assertEquals("Av. Adolfo Lopez Mateos 40", savedHotel.getHotelInfo().getAddress());
        assertEquals(3, savedHotel.getHotelInfo().getRating());

    }

    @Test
    public void shouldGetNotFoundByDeleteById() {
        DeleteHotelRequest request = new DeleteHotelRequest();
        request.setHotelId(1L);
        
        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(null);
        DeleteHotelResponse deletedHotel = hotelService.deleteById(request);

        assertEquals("NOT_FOUND", deletedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found!",deletedHotel.getServiceStatus().getMessage());

    }

    @Test
    public void shouldDeleteById() {
        DeleteHotelRequest request = new DeleteHotelRequest();
        request.setHotelId(1L);

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);

        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(hotel);
        DeleteHotelResponse deletedHotel = hotelService.deleteById(request);
        
        assertEquals("SUCCESS", deletedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel Deleted Succesfully!",deletedHotel.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetBadRequestByCheckUpdateHotel() {
        UpdateHotelRequest request = new UpdateHotelRequest();
        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(1L);
        hotelInfo.setName("");
        hotelInfo.setAddress("");
        hotelInfo.setRating(0);

        request.setHotelInfo(hotelInfo);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("BAD_REQUEST");

        when(errorService.checkHotelInformation(request.getHotelInfo().getName(), request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        UpdateHotelResponse updatedHotel = hotelService.updateHotel(request);

        assertEquals("BAD_REQUEST", updatedHotel.getServiceStatus().getStatusCode());
    }

    @Test
    public void shouldGetNotFoundByUpdateHotel() {
        UpdateHotelRequest request = new UpdateHotelRequest();
        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(1L);
        hotelInfo.setName("Fiesta Americana - Las Americas");
        hotelInfo.setAddress("Av. Adolfo Lopez Mateos 40");
        hotelInfo.setRating(3);
        request.setHotelInfo(hotelInfo);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotelInformation(request.getHotelInfo().getName(), request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findHotelById(request.getHotelInfo().getHotelId())).thenReturn(null);

        UpdateHotelResponse updatedHotel = hotelService.updateHotel(request);

        assertEquals("NOT_FOUND", updatedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found", updatedHotel.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetBadRequestWhenHotelAlreadyExistsByUpdateHotel() {
        UpdateHotelRequest request = new UpdateHotelRequest();
        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(1L);
        hotelInfo.setName("Fiesta Americana - Las Americas");
        hotelInfo.setAddress("Av. Adolfo Lopez Mateos 40");
        hotelInfo.setRating(3);
        request.setHotelInfo(hotelInfo);

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);

        List<Hotel> savedHotels = new ArrayList<>();
        savedHotels.add(hotel);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotelInformation(request.getHotelInfo().getName(), request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findHotelById(request.getHotelInfo().getHotelId())).thenReturn(hotel);
        when(hotelRepository.findAllByName(request.getHotelInfo().getName())).thenReturn(savedHotels);
        UpdateHotelResponse updatedHotel = hotelService.updateHotel(request);


        assertEquals("BAD_REQUEST", updatedHotel.getServiceStatus().getStatusCode());
        assertEquals("Updated Hotel Already Exists!", updatedHotel.getServiceStatus().getMessage());
    }

    @Test
    public void shouldUpdateHotel() {
        UpdateHotelRequest request = new UpdateHotelRequest();
        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(1L);
        hotelInfo.setName("Fiesta Americana - Las Americas");
        hotelInfo.setAddress("Av. Adolfo Lopez Mateos 40");
        hotelInfo.setRating(3);
        request.setHotelInfo(hotelInfo);

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);
        Set<Amenity> amenities= new HashSet<>();
        hotel.setAmenities(amenities);

        List<Hotel> savedHotels = new ArrayList<>();

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotelInformation(request.getHotelInfo().getName(), request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findHotelById(request.getHotelInfo().getHotelId())).thenReturn(hotel);
        when(hotelRepository.findAllByName(request.getHotelInfo().getName())).thenReturn(savedHotels);
        when(hotelRepository.save(Mockito.any())).thenReturn(hotel);
        UpdateHotelResponse updatedHotel = hotelService.updateHotel(request);

        assertEquals("SUCCESS",updatedHotel.getServiceStatus().getStatusCode());
        assertEquals("Hotel Information Updated!", updatedHotel.getServiceStatus().getMessage());
        assertEquals(1L, updatedHotel.getHotelInfo().getHotelId());
        assertEquals("Fiesta Americana - Las Americas", updatedHotel.getHotelInfo().getName());
        assertEquals("Av. Adolfo Lopez Mateos 40", updatedHotel.getHotelInfo().getAddress());
        assertEquals(3, updatedHotel.getHotelInfo().getRating());
    }

    @Test
    public void shouldGetAmenityNotFoundByAddAmenity() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setAmenityId(1L);

        when(amenityService.findAmenityById(1l)).thenReturn(null);
        AddAmenityResponse response = hotelService.addAmenity(request);

        assertEquals("NOT_FOUND", response.getServiceStatus().getStatusCode());
        assertEquals("Amenity Not Found!", response.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetHotelNotFoundByAddAmenity() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setAmenityId(1L);
        request.setHotelId(1L);

        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WIFI");
        
        when(amenityService.findAmenityById(1l)).thenReturn(amenity);
        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(null);

        AddAmenityResponse response = hotelService.addAmenity(request);

        assertEquals("NOT_FOUND", response.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found!", response.getServiceStatus().getMessage());

    }

    @Test
    public void shouldGetBadRequestByAddAmenity() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setAmenityId(1L);
        request.setHotelId(1L);

        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WIFI");

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);
        Set<Amenity> amenities = new HashSet<>();
        amenities.add(amenity);
        hotel.setAmenities(amenities);

        when(amenityService.findAmenityById(1l)).thenReturn(amenity);
        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(hotel);

        AddAmenityResponse response = hotelService.addAmenity(request);

        assertEquals("BAD_REQUEST", response.getServiceStatus().getStatusCode());
        assertEquals("Amenity Already Exist In Hotel!", response.getServiceStatus().getMessage());

    }

    @Test
    public void shouldAddAmenity() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setAmenityId(1L);
        request.setHotelId(1L);

        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WIFI");

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);
        Set<Amenity> amenities= new HashSet<>();
        hotel.setAmenities(amenities);

        when(amenityService.findAmenityById(1l)).thenReturn(amenity);
        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(hotel);

        AddAmenityResponse response = hotelService.addAmenity(request);

        assertEquals("SUCCESS", response.getServiceStatus().getStatusCode());
        assertEquals("Amenity Added To Hotel!", response.getServiceStatus().getMessage());

    }

    @Test
    public void shouldGetHotelNotFoundByDeleteAmenity() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setAmenityId(1L);
        request.setHotelId(1L);
        
        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(null);

        DeleteAmenityResponse response = hotelService.deleteAmenity(request);

        assertEquals("NOT_FOUND", response.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found!", response.getServiceStatus().getMessage());
    }
    
    @Test
    public void shouldGetAmenityNotFoundByDeleteAmenity() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setAmenityId(1L);
        request.setHotelId(1L);
        
        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);

        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenityById(1l)).thenReturn(null);

        DeleteAmenityResponse response = hotelService.deleteAmenity(request);

        assertEquals("NOT_FOUND", response.getServiceStatus().getStatusCode());
        assertEquals("Amenity Not Found!", response.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetBadRequestByDeleteAmenity() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setAmenityId(1L);
        request.setHotelId(1L);

        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WIFI");

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);
        Set<Amenity> amenities= new HashSet<>();
        hotel.setAmenities(amenities);
        
        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenityById(1l)).thenReturn(amenity);

        DeleteAmenityResponse response = hotelService.deleteAmenity(request);

        assertEquals("BAD_REQUEST", response.getServiceStatus().getStatusCode());
        assertEquals("Amenity Not Found In Hotel!", response.getServiceStatus().getMessage());
        
    }

    @Test
    public void shouldDeleteAmenity() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setAmenityId(1L);
        request.setHotelId(1L);

        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WIFI");

        Hotel hotel = new Hotel(1L, "Fiesta Americana - Las Americas", "Av. Adolfo Lopez Mateos 40", 3);
        Set<Amenity> amenities = new HashSet<>();
        amenities.add(amenity);
        hotel.setAmenities(amenities);

        when(hotelRepository.findHotelById(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenityById(1l)).thenReturn(amenity);

        DeleteAmenityResponse response = hotelService.deleteAmenity(request);

        assertEquals("SUCCESS", response.getServiceStatus().getStatusCode());
        assertEquals("Amenity Deleted From Hotel!", response.getServiceStatus().getMessage());
    }

}
