package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.UserAddress;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class UserAddressDto {

    private Long id;
    private String city;
    private String zipCode;
    private String address;
    private String county;
    private String country;

    public static @NotNull UserAddressDto entityToDto(@NotNull UserAddress address) {
        UserAddressDto dto = new UserAddressDto();
        dto.setAddress(address.getAddress());
        dto.setCity(address.getCity());
        dto.setZipCode(address.getZipCode());
        dto.setCounty(address.getCounty());
        dto.setCountry(address.getCountry());
        dto.setId(address.getId());
        return dto;
    }

}
