package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
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
    private String ingredients;
    private boolean availability;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
    private Set<ClientUser> clientUsers;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderProduct> orders;

}
