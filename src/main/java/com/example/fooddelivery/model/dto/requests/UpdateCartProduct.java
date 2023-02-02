package com.example.fooddelivery.model.dto.requests;


import lombok.Data;

@Data
public class UpdateCartProduct {
    private Long clientId;
    private Long productId;
}
