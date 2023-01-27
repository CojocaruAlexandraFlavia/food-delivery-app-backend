package com.example.fooddelivery.model.dto.requests;

import lombok.Data;

@Data
public class AddOrderProductRequest {
    private Long clientId;
    private Long orderProductId;
    private int quantity;
}