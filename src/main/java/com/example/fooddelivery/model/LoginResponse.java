package com.example.fooddelivery.model;

import com.example.fooddelivery.model.dto.user.BaseUserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private BaseUserDto user;

}
