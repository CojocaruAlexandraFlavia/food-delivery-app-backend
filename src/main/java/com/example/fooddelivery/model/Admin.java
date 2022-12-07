package com.example.fooddelivery.model;


import javax.persistence.*;

@Entity
@DiscriminatorValue("admin")
public class Admin extends BaseUser{
}
