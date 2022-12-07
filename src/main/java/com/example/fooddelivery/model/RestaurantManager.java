package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@DiscriminatorValue("restaurant_manager")
@Getter
@Setter
public class RestaurantManager extends BaseUser{

    @OneToMany(mappedBy = "restaurantManager", cascade = CascadeType.ALL)
    private List<Restaurant> restaurants;
}
