package com.choice.soapservice.service;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.choice.hotels.ServiceStatus;


@SpringBootTest
public class ErrorServiceTest {
    @Autowired
    private ErrorService errorService;

    @Test
    void checkHotelComplete(){
        ServiceStatus serviceStatus = errorService.checkHotelInformation("Holiday Inn Express", "Carr. Guadalajara - Chapala 7012", 4);
        assertEquals("CHECKED", serviceStatus.getStatusCode());
    }

    @Test
    void checkHotelBadRequestByEmptyBody(){
        ServiceStatus serviceStatus = errorService.checkHotelInformation(null, null, null);
        assertEquals("Empty request body!", serviceStatus.getMessage());
    }

    @Test
    void checkHotelBadRequestByNoName() {
        ServiceStatus serviceStatus = errorService.checkHotelInformation(null, "Carr. Guadalajara - Chapala 7012", 4);
        assertEquals("Empty hotel name!", serviceStatus.getMessage());
    }

    @Test
    void checkHotelBadRequestByNoAddress() {
        ServiceStatus serviceStatus = errorService.checkHotelInformation("Holiday Inn Express", null, 4);
        assertEquals("Empty hotel address!", serviceStatus.getMessage());
    }

    @Test
    void checkHotelBadRequestByNoRating() {
        ServiceStatus serviceStatus = errorService.checkHotelInformation("Holiday Inn Express", "Carr. Guadalajara - Chapala 7012", null);
        assertEquals("Empty hotel rating!", serviceStatus.getMessage());
    }

    @Test
    void checkPaginationComplete() {
        ServiceStatus serviceStatus = errorService.checkPagination(2, 3);
        assertEquals("CHECKED", serviceStatus.getStatusCode());
    }

    @Test
    void checkPaginationBadRequestByEmptyBody() {
        ServiceStatus serviceStatus = errorService.checkPagination(null, null);
        assertEquals("No pagination provided!", serviceStatus.getMessage());
    }

    @Test
    void checkPaginationBadRequestWithPageNumberLessThanZero() {
        ServiceStatus serviceStatus = errorService.checkPagination(-1, 3);
        assertEquals("Page number must not be less than 0!", serviceStatus.getMessage());
    }

    @Test
    void checkPaginationBadRequestWithPageSizeLessThanOne() {
        ServiceStatus serviceStatus = errorService.checkPagination(2, 0);
        assertEquals("Page size must not be less than 1!", serviceStatus.getMessage());
    }
}
