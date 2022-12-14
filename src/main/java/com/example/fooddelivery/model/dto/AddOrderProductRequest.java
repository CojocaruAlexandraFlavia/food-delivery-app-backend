package com.example.fooddelivery.model.dto;

import lombok.Data;

@Data
public class AddOrderProductRequest {
    private Long orderId;
    private Long orderProductId;
    private int quantity;
}