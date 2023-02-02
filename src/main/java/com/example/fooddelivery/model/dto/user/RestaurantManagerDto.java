package com.example.fooddelivery.model.dto.user;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.RestaurantManager;
import com.example.fooddelivery.model.dto.RestaurantDto;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class RestaurantManagerDto extends BaseUserDto{

    private List<RestaurantDto> ownedRestaurants;

    public static @NotNull RestaurantManagerDto entityToDto(@NotNull RestaurantManager restaurantManager) {
        RestaurantManagerDto dto = new RestaurantManagerDto();
        dto.setId(restaurantManager.getId());
        dto.setEmail(restaurantManager.getEmail());
        dto.setRole(Role.ROLE_RESTAURANT_MANAGER.toString());
        dto.setLastName(restaurantManager.getLastName());
        dto.setFirstName(restaurantManager.getFirstName());
        dto.setPassword(restaurantManager.getPassword());
        dto.setId(restaurantManager.getId());
        dto.setPhoneNumber(restaurantManager.getPhoneNumber());
        dto.setOwnedRestaurants(restaurantManager.getRestaurants()
                .stream().map(RestaurantDto::entityToDto).collect(toList()));
        return dto;
    }

}
