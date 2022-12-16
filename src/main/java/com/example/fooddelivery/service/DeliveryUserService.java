package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.BaseUser;
import com.example.fooddelivery.model.DeliveryUser;
import com.example.fooddelivery.model.dto.user.BaseUserDto;
import com.example.fooddelivery.repository.BaseUserRepository;
import com.example.fooddelivery.repository.DeliveryUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryUserService {

    private final DeliveryUserRepository deliveryUserRepository;
    private final BaseUserRepository baseUserRepository;
    private final PasswordEncoder passwordEncoder;


    public DeliveryUserService(DeliveryUserRepository deliveryUserRepository, BaseUserRepository baseUserRepository,
                               PasswordEncoder passwordEncoder) {
        this.deliveryUserRepository = deliveryUserRepository;
        this.baseUserRepository = baseUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public DeliveryUser registerUser(BaseUserDto dto) {
        Optional<BaseUser> optionalBaseUser = baseUserRepository.findByEmail(dto.getEmail());
        if(optionalBaseUser.isEmpty()) {
            DeliveryUser deliveryUser = new DeliveryUser();
            deliveryUser.setEmail(dto.getEmail());
            deliveryUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            deliveryUser.setFirstName(dto.getFirstName());
            deliveryUser.setLastName(dto.getLastName());
            deliveryUser.setRole(Role.ROLE_DELIVERY_USER);
            deliveryUser = deliveryUserRepository.save(deliveryUser);
            return deliveryUser;
        }
        return null;
    }

    public Optional<DeliveryUser> findDeliveryUserById(Long id){
        return deliveryUserRepository.findById(id);
    }

}
