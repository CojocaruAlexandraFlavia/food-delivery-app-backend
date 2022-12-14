package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Order;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class OrderDto {

    private String status;
    private String number;
    private List<NotificationDto> notifications;
    private List<OrderProductDto> products;
    private Long clientUserId;
    private Long deliveryUserId;
    private Long historyId;

    public static @NotNull OrderDto entityToDto(@NotNull Order order) {
        OrderDto dto = new OrderDto();
        dto.setStatus(order.getStatus());
        dto.setNumber(order.getNumber());

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
        dto.setHistoryId(order.getHistory().getId());
        return dto;
    }

}
