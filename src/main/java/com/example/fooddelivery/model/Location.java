package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "seq_location", sequenceName = "seq_location")
    private Long id;

    private Boolean availability;
    private String city;
    private String address;

    @ManyToOne
    @JoinColumn(
            name = "restaurant_id",
            foreignKey = @ForeignKey(name = "fk_restaurant_location")
    )
    private Restaurant restaurant;
}
