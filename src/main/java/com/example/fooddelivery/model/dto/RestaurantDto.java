package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class RestaurantDto {

    private String name;
    private Long phone_number;
    private Long rating;

    private Long restaurantManagerId;

    private List<LocationDto> locations;

    public static @NotNull RestaurantDto entityToDto(@NotNull Restaurant restaurant) {
        RestaurantDto dto = new RestaurantDto();
        dto.setName(restaurant.getName());
        dto.setPhone_number(restaurant.getPhone_number());
        dto.setRating(restaurant.getRating());

        dto.setRestaurantManagerId(restaurant.getRestaurantManager().getId());

        if(restaurant.getLocations() !=null){
            dto.setLocations(restaurant.getLocations().stream().map(LocationDto::entityToDto).collect(toList()));
        }

        return dto;
    }
}
