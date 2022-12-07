package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private String name;
    private Double discount;
    private Double quantity;
    private String ingredients;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
    private Set<Order> orders;

    @ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
    private Set<ClientUser> clientUsers;

}
