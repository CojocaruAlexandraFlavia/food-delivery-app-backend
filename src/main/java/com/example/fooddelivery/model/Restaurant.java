package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long phone_number;
    private Long rating;

    @ManyToOne
    private RestaurantManager restaurantManager;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Location> locations;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Review> reviews;



}
