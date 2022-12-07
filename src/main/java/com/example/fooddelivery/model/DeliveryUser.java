package com.example.fooddelivery.model;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("delivery_user")
public class DeliveryUser extends BaseUser{

    @OneToOne
    private History history;

    @OneToMany(mappedBy = "deliveryUser", cascade = CascadeType.ALL)
    private List<Order> orders;

}
