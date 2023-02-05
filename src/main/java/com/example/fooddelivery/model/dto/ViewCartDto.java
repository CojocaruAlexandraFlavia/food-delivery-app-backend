package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.enums.PaymentType;
import com.example.fooddelivery.model.Order;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class ViewCartDto {


    private UserAddressDto deliveryAddress;
    private List<OrderProductDto> products;
    private Long number;
    private Double deliveryTax;
    private Double value;
    private Long clientUserId;
    private Long orderId;
    private String paymentType;

    public static @NotNull ViewCartDto entityToDto(@NotNull Order order) {
        ViewCartDto dto = new ViewCartDto();

        dto.setNumber(order.getNumber());
        dto.setDeliveryTax(order.getDeliveryTax());
        dto.setClientUserId(order.getClientUser().getId());
        dto.setOrderId(order.getId());
        if(order.getPaymentType().equals(PaymentType.CASH_ON_DELIVERY)){
            dto.setPaymentType("Cash on delivery");
        }else{
            dto.setPaymentType("Online payment");
        }

        return dto;
    }
}
