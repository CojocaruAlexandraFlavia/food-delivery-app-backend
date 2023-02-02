package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.BaseUser;
import com.example.fooddelivery.model.ClientUser;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.model.dto.requests.AddProductToFavoritesRequest;
import com.example.fooddelivery.model.dto.user.BaseUserDto;
import com.example.fooddelivery.model.dto.user.ClientUserDto;
import com.example.fooddelivery.repository.ClientUserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ClientUserService {

    private final ClientUserRepository clientUserRepository;
    private final BaseUserService baseUserService;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;

    @Autowired
    public ClientUserService(ClientUserRepository clientUserRepository, BaseUserService baseUserService,
                             PasswordEncoder passwordEncoder, ProductService productService) {
        this.clientUserRepository = clientUserRepository;
        this.baseUserService = baseUserService;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
    }

    public ClientUser registerUser(BaseUserDto dto) {
        Optional<BaseUser> optionalBaseUser = baseUserService.findUserByEmail(dto.getEmail());
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
    public Optional<ClientUser> findClientUserById(Long id){
        return clientUserRepository.findById(id);
    }

    public ClientUserDto addProductToFavorite(@NotNull AddProductToFavoritesRequest request) {
        Optional<ClientUser> optionalClientUser = findClientUserById(request.getClientUserId());
        Optional<Product> optionalProduct = productService.findProductById(request.getProductId());
        if (optionalClientUser.isPresent() && optionalProduct.isPresent()) {
            ClientUser clientUser = optionalClientUser.get();
            Product product = optionalProduct.get();
            Optional<Product> alreadyAddedProduct = clientUser.getProducts().stream().filter(existingProduct ->
                    existingProduct.getId().equals(product.getId())).findAny();
            if (alreadyAddedProduct.isEmpty()) {
                clientUser.addProductToFavoritesList(product);
                return ClientUserDto.entityToDto(clientUserRepository.save(clientUser));
            }
        }
        return null;
    }

    public List<ClientUserDto> getAllClientUsers() {
        List<ClientUser> allClientUsers = clientUserRepository.findAll();
        return allClientUsers.stream().map(ClientUserDto::entityToDto).collect(toList());
    }

    public ClientUserDto editClient(Long id, ClientUserDto dto) {
        Optional<ClientUser> optionalClientUser = findClientUserById(id);
        if(optionalClientUser.isPresent()) {
            ClientUser clientUser = optionalClientUser.get();
            Optional<BaseUser> baseUserOptional = baseUserService.findUserByEmail(dto.getEmail());
            if(baseUserOptional.isPresent() && !Objects.equals(clientUser.getId(), baseUserOptional.get().getId())) {
                return null;
            }
            clientUser.setEmail(dto.getEmail());
            clientUser.setFirstName(dto.getFirstName());
            clientUser.setLastName(dto.getLastName());
            clientUser.setPhoneNumber(dto.getPhoneNumber());
            clientUser = clientUserRepository.save(clientUser);
            return ClientUserDto.entityToDto(clientUser);
        }
        return null;
    }
}
