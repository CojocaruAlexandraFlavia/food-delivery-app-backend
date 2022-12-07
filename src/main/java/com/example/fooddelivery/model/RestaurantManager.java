package com.example.fooddelivery.model;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@DiscriminatorValue("restaurant_manager")
public class RestaurantManager extends BaseUser{

    @OneToMany(mappedBy = "restaurantManager", cascade = CascadeType.ALL)
    private List<Restaurant> restaurants;
}
