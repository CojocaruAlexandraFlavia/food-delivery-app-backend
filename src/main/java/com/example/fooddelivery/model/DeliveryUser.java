package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("delivery_user")
@Getter
@Setter
public class DeliveryUser extends BaseUser{

    @OneToMany(mappedBy = "deliveryUser", cascade = CascadeType.ALL)
    private List<Order> orders;

}
