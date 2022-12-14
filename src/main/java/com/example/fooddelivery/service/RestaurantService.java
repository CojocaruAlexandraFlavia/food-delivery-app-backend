package com.example.fooddelivery.service;

import com.example.fooddelivery.model.BaseUser;
import com.example.fooddelivery.model.Location;
import com.example.fooddelivery.model.Restaurant;
import com.example.fooddelivery.model.RestaurantManager;
import com.example.fooddelivery.model.dto.RestaurantDto;
import com.example.fooddelivery.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.fooddelivery.model.dto.RestaurantDto.entityToDto;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    //private final RestaurantManagerService restaurantManagerService;
    private final BaseUserService baseUserService;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, LocationRepository locationRepository,
                             ProductRepository productRepository, ReviewRepository reviewRepository,
                             BaseUserService baseUserService) {
        this.restaurantRepository = restaurantRepository;
        this.locationRepository = locationRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.baseUserService = baseUserService;
    }

    public Optional<Restaurant> findRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public RestaurantDto saveRestaurant(@NotNull RestaurantDto restaurantDto){
        Restaurant restaurant = new Restaurant();
        Optional<BaseUser> optionalRestaurantManager = baseUserService.findUserById(restaurantDto.getRestaurantManagerId());

        if(optionalRestaurantManager.isPresent()){
            //save restaurant
            restaurant.setName(restaurantDto.getName());
            restaurant.setPhone_number(restaurantDto.getPhone_number());
            restaurant.setRating(restaurantDto.getRating());
            restaurant.setRestaurantManager((RestaurantManager) optionalRestaurantManager.get());

            restaurant = restaurantRepository.save(restaurant);
            return entityToDto(restaurant);
        }
        return null;
    }

    public RestaurantDto updateRestaurant(Long restaurantId, RestaurantDto restaurantDto){
        Optional<Restaurant> optionalRestaurant = findRestaurantById(restaurantId);
        if(optionalRestaurant.isPresent()){
            Restaurant restaurant = optionalRestaurant.get();
            restaurant.setName(restaurantDto.getName());
            restaurant.setPhone_number(restaurantDto.getPhone_number());
            restaurant.setRating(restaurantDto.getRating());
            return entityToDto(restaurantRepository.save(restaurant));
        }
        return null;
    }

    public boolean deleteRestaurant(Long restaurantId){
        if(restaurantId != null){
            Optional<Restaurant> optionalRestaurant = findRestaurantById(restaurantId);
            if(optionalRestaurant.isPresent()){
                restaurantRepository.delete(optionalRestaurant.get());
                return true;
            }
        }
        return false;
    }

    public RestaurantDto addLocation(Long restaurantId, Long restaurantManagerId, String address){
        Optional<Restaurant> optionalRestaurant = findRestaurantById(restaurantId);
        Optional<BaseUser> optionalRestaurantManager = baseUserService.findUserById(restaurantManagerId);

        if(optionalRestaurant.isPresent() && optionalRestaurantManager.isPresent()){
            Restaurant restaurant = optionalRestaurant.get();
            Location l = new Location();
            l.setAddress(address);
            l.setRestaurant(restaurant);
            locationRepository.save(l);
            return entityToDto(restaurantRepository.save(restaurant));
        }
        return null;
    }


}
