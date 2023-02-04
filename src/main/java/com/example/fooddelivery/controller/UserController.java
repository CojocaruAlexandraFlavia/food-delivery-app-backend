package com.example.fooddelivery.controller;


import com.example.fooddelivery.model.dto.UserAddressDto;
import com.example.fooddelivery.model.dto.user.AdminUserDto;
import com.example.fooddelivery.model.dto.user.ClientUserDto;
import com.example.fooddelivery.service.BaseUserService;
import com.example.fooddelivery.service.ClientUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
@RequestMapping("/user")
public class UserController {
    private final ClientUserService clientUserService;
    private final BaseUserService baseUserService;

    public UserController(ClientUserService clientUserService, BaseUserService baseUserService) {
        this.clientUserService = clientUserService;
        this.baseUserService = baseUserService;
    }

    @GetMapping("/get-all-clients")
    public ResponseEntity<List<ClientUserDto>> getAllClientUsers(){
        return new ResponseEntity<>(clientUserService.getAllClientUsers(), HttpStatus.OK);
    }

    @PutMapping("/update-client/{id}")
    public ResponseEntity<ClientUserDto> editClient(@PathVariable("id") Long id, @RequestBody ClientUserDto dto) {
        ClientUserDto result = clientUserService.editClient(id, dto);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/update-admin/{id}")
    public ResponseEntity<AdminUserDto> editAdmin(@PathVariable("id") Long id, @RequestBody AdminUserDto dto) {
        AdminUserDto result = baseUserService.updateAdmin(id, dto);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add-client-address/{clientId}")
    public ResponseEntity<ClientUserDto> addUserAddress(@RequestBody UserAddressDto dto,
                                                        @PathVariable("clientId") Long clientId) {
        ClientUserDto result = clientUserService.addClientAddress(dto, clientId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @PutMapping("/edit-address/{id}")
    public ResponseEntity<ClientUserDto> editAddress(@RequestBody UserAddressDto dto,
                                                     @PathVariable("id") Long addressId) {
        ClientUserDto result = clientUserService.editAddress(dto, addressId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/delete-address/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") Long id) {
        ClientUserDto result = clientUserService.deleteAddress(id);
        if(result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
