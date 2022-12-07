package com.example.fooddelivery.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("delivery_user")
public class DeliveryUser extends BaseUser{
}
