package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.BaseUser;
import com.example.fooddelivery.model.ClientUser;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.model.UserAddress;
import com.example.fooddelivery.model.dto.UserAddressDto;
import com.example.fooddelivery.model.dto.requests.AddProductToFavoritesRequest;
import com.example.fooddelivery.model.dto.user.BaseUserDto;
import com.example.fooddelivery.model.dto.user.ClientUserDto;
import com.example.fooddelivery.repository.ClientUserRepository;
import com.example.fooddelivery.repository.UserAddressRepository;
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
    private final UserAddressRepository addressRepository;

    @Autowired
    public ClientUserService(ClientUserRepository clientUserRepository, BaseUserService baseUserService,
                             PasswordEncoder passwordEncoder, ProductService productService,
                             UserAddressRepository addressRepository) {
        this.clientUserRepository = clientUserRepository;
        this.baseUserService = baseUserService;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
        this.addressRepository = addressRepository;
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
            clientUser.setPreferredCity(dto.getPreferredCity());
            clientUser.setPhoneNumber(dto.getPhoneNumber());
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

    public ClientUserDto addClientAddress(UserAddressDto dto, Long clientId) {
        Optional<ClientUser> optionalClientUser = findClientUserById(clientId);
        if(optionalClientUser.isPresent()) {
            ClientUser clientUser = optionalClientUser.get();
            UserAddress address = new UserAddress();
            address.setClientUser(clientUser);
            address.setAddress(dto.getAddress());
            address.setCity(dto.getCity());
            address.setZipCode(dto.getZipCode());
            address = addressRepository.save(address);
            clientUser.addAddress(address);
            clientUser = clientUserRepository.save(clientUser);
            return ClientUserDto.entityToDto(clientUser);
        }
        return null;
    }

    public ClientUserDto editAddress(UserAddressDto dto, Long addressId) {
        Optional<UserAddress> optionalUserAddress = addressRepository.findById(addressId);
        if(optionalUserAddress.isPresent()) {
            UserAddress userAddress = optionalUserAddress.get();
            userAddress.setAddress(dto.getAddress());
            userAddress.setCity(dto.getCity());
            userAddress.setZipCode(dto.getZipCode());
            userAddress = addressRepository.save(userAddress);
            ClientUser clientUser = userAddress.getClientUser();
            clientUser.addAddress(userAddress);
            clientUser = clientUserRepository.save(clientUser);
            return ClientUserDto.entityToDto(clientUser);
        }
        return null;
    }

    public ClientUserDto deleteAddress(Long id) {
        Optional<UserAddress> optionalUserAddress = addressRepository.findById(id);
        if(optionalUserAddress.isPresent()) {
            UserAddress userAddress = optionalUserAddress.get();
            addressRepository.delete(userAddress);
            ClientUser clientUser = userAddress.getClientUser();
            clientUser.deleteAddress(userAddress);
            clientUser = clientUserRepository.save(clientUser);
            return ClientUserDto.entityToDto(clientUser);
        }
        return null;
    }
}
