package com.example.fooddelivery.model.dto.user;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.DeliveryUser;
import com.example.fooddelivery.model.dto.OrderDto;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class DeliveryUserDto extends BaseUserDto{

    private List<OrderDto> orders;

    public static @NotNull DeliveryUserDto entityToDto(@NotNull DeliveryUser deliveryUser) {
        DeliveryUserDto dto = new DeliveryUserDto();
        dto.setEmail(deliveryUser.getEmail());
        dto.setLastName(deliveryUser.getLastName());
        dto.setFirstName(deliveryUser.getFirstName());
        dto.setRole(Role.ROLE_DELIVERY_USER.toString());
        dto.setPassword(deliveryUser.getPassword());

        if(deliveryUser.getOrders() != null) {
            dto.setOrders(deliveryUser.getOrders()
                    .stream().map(OrderDto::entityToDto).collect(toList()));
        } else {
            dto.setOrders(new ArrayList<>());
        }

        return dto;
    }

}
