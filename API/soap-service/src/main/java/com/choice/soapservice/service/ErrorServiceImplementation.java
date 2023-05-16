
package com.choice.soapservice.service;

import org.springframework.stereotype.Service;

import com.choice.hotels.ServiceStatus;

@Service
public class ErrorServiceImplementation implements ErrorService{
    @Override
    public ServiceStatus checkHotelInformation(String name, String address, Integer rating){
        ServiceStatus serviceStatus = new ServiceStatus();
        if((name == null || name.length() < 1) && (address == null || address.length() < 1) && (rating == null || rating == 0)) {
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Empty request body!");
            return serviceStatus;
        }

        if(name == null || name.length() < 1) {
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Empty hotel name!");
            return serviceStatus;
        }

        if(address == null || address.length() < 1) {
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Empty hotel address!");
            return serviceStatus;

        }

        if(rating == null) {
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Empty hotel rating!");
            return serviceStatus;
        }

        if(rating < 1 || rating > 5) {
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Rating must be in a range from 1 to 5!");
            return serviceStatus;
        }

        serviceStatus.setStatusCode("CHECKED");
        serviceStatus.setMessage("Ok");
        return serviceStatus;

    }

    @Override
    public ServiceStatus checkPagination(Integer pageNumber, Integer pageSize) {
        ServiceStatus serviceStatus = new ServiceStatus();

        if((pageNumber == null || pageNumber == 0) && (pageSize == null || pageSize == 0)){
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("No pagination provided!");
            return serviceStatus;
        }

        if(pageNumber == null || pageNumber < 0){
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Page number must not be less than 0!");
            return serviceStatus;
        }

        if(pageSize == null || pageSize < 1){
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage("Page size must not be less than 1!");
            return serviceStatus;
        }

        serviceStatus.setStatusCode("CHECKED");
        serviceStatus.setMessage("Ok");
        return serviceStatus;
    }
}
