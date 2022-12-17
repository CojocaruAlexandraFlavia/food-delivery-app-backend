package com.example.fooddelivery.controller;

import com.example.fooddelivery.model.Restaurant;
import com.example.fooddelivery.model.dto.ReviewDto;
import com.example.fooddelivery.model.dto.requests.AddLocationRequest;
import com.example.fooddelivery.model.dto.RestaurantDto;
import com.example.fooddelivery.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Transactional
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/save")
    public ResponseEntity<RestaurantDto> saveRestaurant(@RequestBody RestaurantDto restaurantDto){
        return ResponseEntity.of(Optional.of(restaurantService.saveRestaurant(restaurantDto)));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<RestaurantDto> findRestaurantById(@PathVariable("id") Long id){
        Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);
        if(optionalRestaurant.isPresent()){
            RestaurantDto restaurantDto = RestaurantDto.entityToDto(optionalRestaurant.get());
            return new ResponseEntity<>(restaurantDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<?> deleteRestaurantById(@PathVariable("id") Long id){
        boolean result = restaurantService.deleteRestaurant(id);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable("id") Long id,
                                                          @RequestBody RestaurantDto dto){
        RestaurantDto result = restaurantService.updateRestaurant(id, dto);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/add-location")
    public ResponseEntity<RestaurantDto> addLocation(@RequestBody AddLocationRequest addLocationRequest){
        RestaurantDto result = restaurantService.addLocation(addLocationRequest.getRestaurantId(),
                addLocationRequest.getRestaurantManagerId(), addLocationRequest.getLocation());
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @GetMapping("/get-all")
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        return new ResponseEntity<>(restaurantService.getAllRestaurants(), HttpStatus.OK);
    }

    @PostMapping("/add-review")
    public ResponseEntity<ReviewDto> addClientReview(@RequestBody ReviewDto review) {
        ReviewDto result = restaurantService.addClientReview(review);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
