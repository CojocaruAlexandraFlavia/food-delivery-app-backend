package com.example.fooddelivery.model;

import com.example.fooddelivery.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean seen;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne
    private Order order;

}
