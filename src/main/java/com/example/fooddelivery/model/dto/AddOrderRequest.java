package com.example.fooddelivery.model.dto;

import lombok.Data;

@Data
public class AddOrderRequest {
    private Long historyId;
    private Long clientUserId;
    private Long deliveryUserId;
    private String number;
}