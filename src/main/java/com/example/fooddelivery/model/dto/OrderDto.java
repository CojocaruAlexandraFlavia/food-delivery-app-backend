package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto {

    private String status;
    private Long number;
    private Double deliveryTax;
    private Double value;

    private List<NotificationDto> notifications;
    private List<OrderProductDto> products;

    private Long clientUserId;
    private Long deliveryUserId;

    public static @NotNull OrderDto entityToDto(@NotNull Order order) {
        OrderDto dto = new OrderDto();
        dto.setStatus(order.getStatus().toString());
        dto.setNumber(order.getNumber());
        dto.setValue(order.getValue());

        if(order.getProducts() != null) {
            dto.setProducts(order.getProducts()
                    .stream().map(OrderProductDto::entityToDto).collect(toList()));
        } else {
            dto.setProducts(new ArrayList<>());
        }
        if(order.getNotifications() != null) {
            dto.setNotifications(order.getNotifications()
                    .stream().map(NotificationDto::entityToDto).collect(toList()));
        } else {
            dto.setNotifications(new ArrayList<>());
        }
        dto.setClientUserId(order.getClientUser().getId());
        dto.setDeliveryUserId(order.getDeliveryUser().getId());
        return dto;
    }

}
