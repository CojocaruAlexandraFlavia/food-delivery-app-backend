package com.example.fooddelivery.model.dto.user;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.RestaurantManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class RestaurantManagerDto extends BaseUserDto{

    public static @NotNull RestaurantManagerDto entityToDto(@NotNull RestaurantManager restaurantManager) {
        RestaurantManagerDto dto = new RestaurantManagerDto();
        dto.setEmail(restaurantManager.getEmail());
        dto.setRole(Role.ROLE_RESTAURANT_MANAGER.toString());
        dto.setLastName(restaurantManager.getLastName());
        dto.setFirstName(restaurantManager.getFirstName());
        dto.setPassword(restaurantManager.getPassword());
        return dto;
    }

}
