package com.example.fooddelivery.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("restaurant_manager")
public class RestaurantManager extends BaseUser{
}
