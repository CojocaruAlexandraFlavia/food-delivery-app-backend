package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.Role;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.LocationDto;
import com.example.fooddelivery.model.dto.RestaurantDto;
import com.example.fooddelivery.model.dto.ReviewDto;
import com.example.fooddelivery.model.dto.user.BaseUserDto;
import com.example.fooddelivery.model.dto.user.DeliveryUserDto;
import com.example.fooddelivery.model.dto.user.RestaurantManagerDto;
import com.example.fooddelivery.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final ReviewRepository reviewRepository;
    private final BaseUserRepository baseUserRepository;

    private final BaseUserService baseUserService;
    private final PasswordEncoder passwordEncoder;

    private final RestaurantManagerRepository restaurantManagerRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, LocationRepository locationRepository,
                             ReviewRepository reviewRepository, BaseUserRepository baseUserRepository,
                             BaseUserService baseUserService, PasswordEncoder passwordEncoder,
                             RestaurantManagerRepository restaurantManagerRepository) {
        this.restaurantRepository = restaurantRepository;
        this.locationRepository = locationRepository;
        this.reviewRepository = reviewRepository;
        this.baseUserRepository = baseUserRepository;
        this.baseUserService = baseUserService;
        this.passwordEncoder = passwordEncoder;
        this.restaurantManagerRepository = restaurantManagerRepository;
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
            restaurant.setDeliveryTax(restaurantDto.getDeliveryTax());
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
            location.setAvailability(true);
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

    public RestaurantManagerDto addManagerUser(@NotNull BaseUserDto dto) {
        Optional<BaseUser> optionalBaseUser = baseUserRepository.findByEmail(dto.getEmail());
        if(optionalBaseUser.isEmpty()) {
            RestaurantManager user = new RestaurantManager();
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getFirstName() + dto.getLastName()));
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setRole(Role.ROLE_RESTAURANT_MANAGER);
            user = baseUserRepository.save(user);
            return RestaurantManagerDto.entityToDto(user);
        }
        return null;
    }

    public DeliveryUserDto addDeliveryUser(@NotNull BaseUserDto dto) {
        Optional<BaseUser> optionalBaseUser = baseUserRepository.findByEmail(dto.getEmail());
        if(optionalBaseUser.isEmpty()) {
            DeliveryUser user = new DeliveryUser();
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getFirstName()) + dto.getLastName());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setRole(Role.ROLE_DELIVERY_USER);
            user.setPreferredCity(dto.getPreferredCity());
            user = baseUserRepository.save(user);
            return DeliveryUserDto.entityToDto(user);
        }
        return null;
    }

    public List<RestaurantManagerDto> getAllRestaurantManagers() {
        List<RestaurantManager> restaurantManagers = restaurantManagerRepository.findAll();
        return restaurantManagers.stream().map(RestaurantManagerDto::entityToDto).collect(toList());
    }

    public List<RestaurantDto> changeLocationAvailability(Long id) {
        Optional<Location> optionalLocation = locationRepository.findById(id);
        if(optionalLocation.isPresent()) {
            Location location = optionalLocation.get();
            location.setAvailability(!location.getAvailability());
            locationRepository.save(location);
        }
        return getAllRestaurants();
    }

    public List<RestaurantDto> getAllByCity(String city) {
        List<RestaurantDto> restaurants = getAllRestaurants();
        return restaurants.stream().filter(restaurantDto -> restaurantDto.getLocations()
                .stream().anyMatch(locationDto -> locationDto.getCity().equalsIgnoreCase(city)))
                .collect(toList());
    }
}
