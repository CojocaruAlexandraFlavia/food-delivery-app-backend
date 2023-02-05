package com.example.fooddelivery.model.dto.requests;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOrder {
    private Long clientId;
    private Long addressId;
}
