package com.example.fooddelivery.model.dto.requests;

import com.example.fooddelivery.model.dto.LocationDto;
import lombok.Data;

@Data
public class AddLocationRequest {
    private Long restaurantId;
    private Long restaurantManagerId;
    private LocationDto location;
}
