package com.example.fooddelivery.model.dto.user;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.DeliveryUser;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class DeliveryUserDto extends BaseUserDto{

    public static @NotNull DeliveryUserDto entityToDto(@NotNull DeliveryUser deliveryUser) {
        DeliveryUserDto dto = new DeliveryUserDto();
        dto.setEmail(deliveryUser.getEmail());
        dto.setLastName(deliveryUser.getLastName());
        dto.setFirstName(deliveryUser.getFirstName());
        dto.setRole(Role.ROLE_DELIVERY_USER.toString());
        dto.setPassword(deliveryUser.getPassword());
        return dto;
    }

}
