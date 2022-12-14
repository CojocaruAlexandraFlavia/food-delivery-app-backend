package com.example.fooddelivery.model.dto;

import lombok.Data;

@Data
public class AddLocationRequest {
    private Long restaurantId;
    private Long restaurantManagerId;
    private String address;
}
