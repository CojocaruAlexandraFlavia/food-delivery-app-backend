package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    private Delivery_User delivery_user_id;

    private String status;

//    @ManyToOne
//    private User user_id;

//    @ManyToOne
//    private History history_id;
}
