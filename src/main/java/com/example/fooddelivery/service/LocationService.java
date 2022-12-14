package com.example.fooddelivery.service;

import com.example.fooddelivery.model.Location;
import com.example.fooddelivery.model.Restaurant;
import com.example.fooddelivery.model.dto.LocationDto;
import com.example.fooddelivery.repository.LocationRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final RestaurantService restaurantService;

    @Autowired
    public LocationService(LocationRepository locationRepository, RestaurantService restaurantService) {
        this.locationRepository = locationRepository;
        this.restaurantService = restaurantService;
    }

    public Optional<Location> findLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public LocationDto saveLocation(@NotNull LocationDto locationDto){
        Location location = new Location();
        Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(locationDto.getRestaurantId());

        if(optionalRestaurant.isPresent()){
            //saving location
            location.setAvailability(locationDto.getAvailability());
            location.setCity(locationDto.getCity());
            location.setAddress(locationDto.getAddress());
            location.setRestaurant(optionalRestaurant.get());

            location = locationRepository.save(location);
            return LocationDto.entityToDto(location);
        }
        return null;
    }

    public LocationDto updateLocation(Long locationId, LocationDto locationDto){
        Optional<Location> optionalLocation = findLocationById(locationId);
        if(optionalLocation.isPresent()){
            Location location = optionalLocation.get();
            location.setAvailability(locationDto.getAvailability());
            location.setCity(locationDto.getCity());
            location.setAddress(locationDto.getAddress());
            return LocationDto.entityToDto(locationRepository.save(location));
        }
        return null;
    }

    @Transactional
    public boolean deleteLocation(Long locationId){
        Optional<Location> optionalLocation = findLocationById(locationId);
        if(optionalLocation.isPresent()){
            locationRepository.delete(optionalLocation.get());
            return true;
        }
        return false;
    }

}
