package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    private ClientUser clientUser;

    @OneToOne
    private DeliveryUser deliveryUser;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL)
    private List<Order> orders;

}
