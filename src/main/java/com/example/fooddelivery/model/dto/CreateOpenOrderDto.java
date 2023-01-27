package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOpenOrderDto {
    private Long id;
    private String status;
    private Long number;
    private Double deliveryTax;
    private String dateTime;
    private Long clientUserId;
    private Long deliveryUserId;


    public static @NotNull CreateOpenOrderDto entityToDto(@NotNull Order order) {
        CreateOpenOrderDto dto = new CreateOpenOrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().toString());
        dto.setNumber(order.getNumber());
        dto.setDeliveryTax(order.getDeliveryTax());
        dto.setDateTime(order.getDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        dto.setClientUserId(order.getClientUser().getId());
        dto.setDeliveryUserId(order.getDeliveryUser().getId());
        return dto;
    }
}
