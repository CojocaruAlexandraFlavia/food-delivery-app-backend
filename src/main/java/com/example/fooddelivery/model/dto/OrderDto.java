package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto {

    private Long id;
    private String status;
    private Long number;
    private Double deliveryTax;
    private Double value;
    private String paymentType;
    private String dateTime;

    private List<NotificationDto> notifications;
    private List<OrderProductDto> products;

    private Long clientUserId;
    private Long deliveryUserId;
    private UserAddressDto deliveryAddress;

    public static @NotNull OrderDto entityToDto(@NotNull Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().toString());
        dto.setNumber(order.getNumber());
        dto.setValue(order.getValue());
        dto.setPaymentType(order.getPaymentType().toString());
        dto.setDeliveryTax(order.getDeliveryTax());
        dto.setDateTime(order.getDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        dto.setDeliveryAddress(UserAddressDto.entityToDto(order.getDeliveryAddress()));

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

        if(order.getDeliveryUser() != null) {
            dto.setDeliveryUserId(order.getDeliveryUser().getId());
        } else {
            dto.setDeliveryUserId(null);
        }

        return dto;
    }

}
