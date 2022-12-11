package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.UserAddress;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class UserAddressDto {

    private String city;
    private Integer zipCode;
    private String address;

    public static @NotNull UserAddressDto entityToDto(@NotNull UserAddress address) {
        UserAddressDto dto = new UserAddressDto();
        dto.setAddress(address.getAddress());
        dto.setCity(address.getCity());
        dto.setZipCode(address.getZipCode());
        return dto;
    }

}
