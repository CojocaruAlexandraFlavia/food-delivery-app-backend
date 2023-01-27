package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Order;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class ViewCartDto {

    private Long id;
    private UserAddressDto deliveryAddress;
    private List<OrderProductDto> products;
    private Long number;
    private Double deliveryTax;
    private Double value;
    private Long clientUserId;

    public static @NotNull ViewCartDto entityToDto(@NotNull Order order) {
        ViewCartDto dto = new ViewCartDto();
        dto.setId(order.getId());
        dto.setNumber(order.getNumber());
        dto.setDeliveryTax(order.getDeliveryTax());
        dto.setClientUserId(order.getClientUser().getId());
        return dto;
    }
}
