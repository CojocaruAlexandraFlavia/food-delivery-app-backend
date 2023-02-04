package com.example.fooddelivery.model.dto.requests;

import lombok.Data;

@Data
public class AddUserAddressRequest {
    private Long clientId;
    private String city;
    private Integer zipCode;
    private String address;
}
