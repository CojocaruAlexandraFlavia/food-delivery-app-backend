package com.example.fooddelivery.model;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@DiscriminatorValue("client_user")
public class ClientUser extends BaseUser {

    @OneToMany(mappedBy = "clientUser", cascade = CascadeType.ALL)
    private List<UserAddress> addresses;
}
