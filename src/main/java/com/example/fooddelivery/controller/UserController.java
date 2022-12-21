package com.example.fooddelivery.controller;


import com.example.fooddelivery.model.ClientUser;
import com.example.fooddelivery.model.dto.user.ClientUserDto;
import com.example.fooddelivery.service.ClientUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/user")
public class UserController {
    private final ClientUserService clientUserService;

    public UserController(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    @GetMapping("/get-all-clients")
    public ResponseEntity<List<ClientUserDto>> getAllClientUsers(){
        return new ResponseEntity<>(clientUserService.getAllClientUsers(), HttpStatus.OK);
    }
}
