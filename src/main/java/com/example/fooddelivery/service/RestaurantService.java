package com.example.fooddelivery.service;

import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.LocationDto;
import com.example.fooddelivery.model.dto.RestaurantDto;
import com.example.fooddelivery.model.dto.ReviewDto;
import com.example.fooddelivery.repository.LocationRepository;
import com.example.fooddelivery.repository.ProductRepository;
import com.example.fooddelivery.repository.RestaurantRepository;
import com.example.fooddelivery.repository.ReviewRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.fooddelivery.model.dto.RestaurantDto.entityToDto;
import static java.util.stream.Collectors.toList;

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
            RestaurantManager restaurantManager = (RestaurantManager) optionalRestaurantManager.get();
            restaurant.setName(restaurantDto.getName());
            restaurant.setPhoneNumber(restaurantDto.getPhoneNumber());
            restaurant.setRating(0.0);
            restaurant.setRestaurantManager(restaurantManager);
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);

            //save restaurant locations
            List<Location> locations = new ArrayList<>();
            restaurantDto.getLocations().forEach(locationDto -> {
                Location location = new Location();
                location.setRestaurant(savedRestaurant);
                location.setAddress(locationDto.getAddress());
                location.setCity(locationDto.getCity());
                location.setAvailability(true);
                locations.add(location);
            });
            List<Location> savedRestaurantLocations = locationRepository.saveAll(locations);
            savedRestaurant.setLocations(savedRestaurantLocations);
            return entityToDto(restaurantRepository.save(savedRestaurant));
        }
        return null;
    }

    public RestaurantDto updateRestaurant(Long restaurantId, RestaurantDto restaurantDto){
        Optional<Restaurant> optionalRestaurant = findRestaurantById(restaurantId);
        if(optionalRestaurant.isPresent()){
            Restaurant restaurant = optionalRestaurant.get();
            restaurant.setName(restaurantDto.getName());
            restaurant.setPhoneNumber(restaurantDto.getPhoneNumber());
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

    public RestaurantDto addLocation(Long restaurantId, Long restaurantManagerId, LocationDto locationDto){
        Optional<Restaurant> optionalRestaurant = findRestaurantById(restaurantId);
        Optional<BaseUser> optionalRestaurantManager = baseUserService.findUserById(restaurantManagerId);

        if(optionalRestaurant.isPresent() && optionalRestaurantManager.isPresent()){
            Restaurant restaurant = optionalRestaurant.get();
            Location location = new Location();
            location.setAddress(locationDto.getAddress());
            location.setCity(locationDto.getCity());
            location.setRestaurant(restaurant);
            locationRepository.save(location);
            return entityToDto(restaurantRepository.save(restaurant));
        }
        return null;
    }


    public List<RestaurantDto> getAllRestaurants() {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        return allRestaurants.stream().map(RestaurantDto::entityToDto).collect(toList());
    }

    public ReviewDto addClientReview(@NotNull ReviewDto dto) {
        Optional<Restaurant> optionalRestaurant = findRestaurantById(dto.getRestaurantId());
        Optional<BaseUser> optionalBaseUser = baseUserService.findUserById(dto.getClientUserId());
        if (optionalRestaurant.isPresent() && optionalBaseUser.isPresent()) {
            ClientUser clientUser = (ClientUser) optionalBaseUser.get();

            //check if client already added review for restaurant
            Optional<Review> alreadyAddedReviewForRestaurant = clientUser.getReviews().stream().filter(
                    review -> review.getClientUser().getId().equals(clientUser.getId()) &&
                            review.getRestaurant().getId().equals(dto.getRestaurantId())).findAny();
            if (alreadyAddedReviewForRestaurant.isEmpty()) {
                Restaurant restaurant = optionalRestaurant.get();

                //update restaurant rating
                int totalNumberOfStars = restaurant.getReviews().stream().mapToInt(Review::getStars).sum();
                totalNumberOfStars += dto.getStars();
                int totalNumberOfReviews = restaurant.getReviews().size() + 1;
                restaurant.setRating( (double) totalNumberOfStars / totalNumberOfReviews);
                restaurant = restaurantRepository.save(restaurant);

                //save review
                Review review = new Review();
                review.setRestaurant(restaurant);
                review.setClientUser(clientUser);
                review.setStars(dto.getStars());
                review.setComment(dto.getComment());
                return ReviewDto.entityToDto(reviewRepository.save(review));
            }
        }
        return null;
    }
}