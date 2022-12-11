package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.BaseUser;
import com.example.fooddelivery.model.ClientUser;
import com.example.fooddelivery.model.dto.BaseUserDto;
import com.example.fooddelivery.repository.BaseUserRepository;
import com.example.fooddelivery.repository.ClientUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientUserService {

    private final ClientUserRepository clientUserRepository;
    private final BaseUserRepository baseUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientUserService(ClientUserRepository clientUserRepository, BaseUserRepository baseUserRepository,
                             PasswordEncoder passwordEncoder) {
        this.clientUserRepository = clientUserRepository;
        this.baseUserRepository = baseUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClientUser registerUser(BaseUserDto dto) {
        Optional<BaseUser> optionalBaseUser = baseUserRepository.findByEmail(dto.getEmail());
        if(optionalBaseUser.isEmpty()) {
            ClientUser clientUser = new ClientUser();
            clientUser.setEmail(dto.getEmail());
            clientUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            clientUser.setFirstName(dto.getFirstName());
            clientUser.setLastName(dto.getLastName());
            clientUser.setRole(Role.ROLE_CLIENT_USER);
            clientUser = clientUserRepository.save(clientUser);
            return clientUser;
        }
        return null;
    }
}
