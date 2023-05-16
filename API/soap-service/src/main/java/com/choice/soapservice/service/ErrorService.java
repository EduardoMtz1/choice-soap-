/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.choice.soapservice.service;

import com.choice.hotels.ServiceStatus;

/**
 *
 * @author isai.martinez
 */
public interface ErrorService {
    public ServiceStatus checkHotelInformation(String name, String address, Integer rating);
    public ServiceStatus checkPagination(Integer pageNumber, Integer pageSize); 
}
