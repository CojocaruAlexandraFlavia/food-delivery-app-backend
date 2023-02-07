package com.example.fooddelivery.service;

import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.requests.UsernameAndPasswordAuthRequest;
import com.example.fooddelivery.model.dto.user.*;
import com.example.fooddelivery.repository.BaseUserRepository;
import com.example.fooddelivery.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class BaseUserService implements UserDetailsService {

    private final BaseUserRepository baseUserRepository;
    private final JwtUtils jwtUtils;

    @Autowired
    public BaseUserService(BaseUserRepository baseUserRepository, JwtUtils jwtUtils) {
        this.baseUserRepository = baseUserRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<BaseUser> baseUserOptional = findUserByEmail(email);
        if(baseUserOptional.isPresent()){
            BaseUser baseUser = baseUserOptional.get();
            return new User(baseUser.getEmail(), baseUser.getPassword(), baseUser.getAuthorities());
        } else throw new UsernameNotFoundException(email);
    }

    public Optional<BaseUser> findUserByEmail(String email) {
        return baseUserRepository.findByEmail(email);
    }

    public Optional<BaseUser> findUserById(Long id){
        return baseUserRepository.findById(id);
    }

    @Transactional
    public BaseUserDto getInfoFromToken(String token) {
        if(jwtUtils.validateJwtToken(token)) {
            String userEmail = jwtUtils.getEmailFromToken(token);
            Optional<BaseUser> optionalBaseUser = findUserByEmail(userEmail);
            if(optionalBaseUser.isPresent()) {
                BaseUser baseUser = optionalBaseUser.get();
                return switchByRole(baseUser);
            }
            return null;
        }
        return null;
    }

    @Transactional
    public Optional<BaseUserDto> login(@NotNull UsernameAndPasswordAuthRequest request) {
        Optional<BaseUser> optionalUser = findUserByEmail(request.getEmail());
        if(optionalUser.isPresent()) {
            BaseUser baseUser = optionalUser.get();
            return Optional.of(switchByRole(baseUser));
        }
        return Optional.empty();
    }

    private @NotNull BaseUserDto switchByRole(BaseUser baseUser) {
        if (baseUser instanceof ClientUser) {
            return ClientUserDto.entityToDto((ClientUser) baseUser);
        } else if (baseUser instanceof Admin) {
            return AdminUserDto.entityToDto((Admin) baseUser);
        } else if (baseUser instanceof DeliveryUser) {
            return DeliveryUserDto.entityToDto((DeliveryUser) baseUser);
        } else {
            return RestaurantManagerDto.entityToDto((RestaurantManager) baseUser);
        }
    }

    public AdminUserDto updateAdmin(Long id, AdminUserDto dto) {
        Optional<BaseUser> optionalBaseUser = findUserById(id);
        if(optionalBaseUser.isPresent()) {
            Admin admin = (Admin) optionalBaseUser.get();
            Optional<BaseUser> baseUserOptional = findUserByEmail(dto.getEmail());
            if(baseUserOptional.isPresent() && !Objects.equals(admin.getId(), baseUserOptional.get().getId())) {
                return null;
            }
            admin.setFirstName(dto.getFirstName());
            admin.setLastName(dto.getLastName());
            admin.setEmail(dto.getEmail());
            admin.setPhoneNumber(dto.getPhoneNumber());
            admin = baseUserRepository.save(admin);
            return AdminUserDto.entityToDto(admin);
        }
        return null;
    }

    public BaseUserDto changePreferredCity(String city, Long userId) {
        Optional<BaseUser> optionalBaseUser = findUserById(userId);
        if(optionalBaseUser.isPresent()) {
            BaseUser baseUser = optionalBaseUser.get();
            baseUser.setPreferredCity(StringUtils.capitalize(city.toLowerCase()));
            baseUser = baseUserRepository.save(baseUser);
            if (baseUser instanceof ClientUser) {
                return ClientUserDto.entityToDto((ClientUser) baseUser);
            } else if (baseUser instanceof DeliveryUser) {
                return DeliveryUserDto.entityToDto((DeliveryUser) baseUser);
            }
        }
        return null;
    }
}
