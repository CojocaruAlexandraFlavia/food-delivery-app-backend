package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double stars;
    private String comment;

    @ManyToOne
    private ClientUser clientUser;

    @ManyToOne
    private Restaurant restaurant;

}
