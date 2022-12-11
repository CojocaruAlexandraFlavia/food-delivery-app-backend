package com.example.fooddelivery.controller;

import com.example.fooddelivery.model.ClientUser;
import com.example.fooddelivery.model.dto.BaseUserDto;
import com.example.fooddelivery.model.dto.ClientUserDto;
import com.example.fooddelivery.service.BaseUserService;
import com.example.fooddelivery.model.BaseUser;
import com.example.fooddelivery.model.LoginResponse;
import com.example.fooddelivery.model.UsernameAndPasswordAuthRequest;
import com.example.fooddelivery.service.ClientUserService;
import com.example.fooddelivery.util.JwtUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthenticationController {

    private final BaseUserService baseUserService;
    private final ClientUserService clientUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(BaseUserService baseUserService, ClientUserService clientUserService,
                                    PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                                    AuthenticationManager authenticationManager) {
        this.baseUserService = baseUserService;
        this.clientUserService = clientUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@NotNull @RequestBody UsernameAndPasswordAuthRequest request) {

        Optional<BaseUser> optionalUser = baseUserService.findUserByEmail(request.getEmail());
        if(optionalUser.isEmpty() ||
                !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ClientUserDto> register(@RequestBody BaseUserDto dto) {
        ClientUser user = clientUserService.registerUser(dto);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(ClientUserDto.entityToDto(user), HttpStatus.OK);
    }

}
