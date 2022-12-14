package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class LocationDto {
    private Boolean availability;
    private String city;
    private String address;
    private Long restaurantId;

    public static @NotNull LocationDto convertEntityToDto(@NotNull Location location) {
        LocationDto dto = new LocationDto();
        dto.setAvailability(location.getAvailability());
        dto.setCity(location.getCity());
        dto.setAddress(location.getAddress());
        dto.setRestaurantId(location.getRestaurant().getId());
        return dto;
    }
}
