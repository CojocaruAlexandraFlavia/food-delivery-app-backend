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
    @SequenceGenerator(name = "seq_review", sequenceName = "seq_review")
    private Long id;
    private int stars;
    private String message;

    @ManyToOne
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(
                    name = "fk_client_review"
            )
    )
    private ClientUser clientUser;

    @ManyToOne
    @JoinColumn(
            name = "restaurant_id",
            foreignKey = @ForeignKey(name = "fk_restaurant_review")
    )
    private Restaurant restaurant;

}
