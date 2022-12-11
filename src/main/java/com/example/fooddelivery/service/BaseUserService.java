package com.example.fooddelivery.service;

import com.example.fooddelivery.model.BaseUser;
import com.example.fooddelivery.repository.BaseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BaseUserService implements UserDetailsService {

    private final BaseUserRepository baseUserRepository;

    @Autowired
    public BaseUserService(BaseUserRepository baseUserRepository) {
        this.baseUserRepository = baseUserRepository;
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

}
