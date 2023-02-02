package com.example.fooddelivery.model.dto.user;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.Admin;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class AdminUserDto extends BaseUserDto{

    public static @NotNull AdminUserDto entityToDto(@NotNull Admin admin) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(admin.getId());
        dto.setEmail(admin.getEmail());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setRole(Role.ROLE_ADMIN.toString());
        dto.setPassword(admin.getPassword());
        dto.setPhoneNumber(admin.getPhoneNumber());
        return dto;
    }

}
