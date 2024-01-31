package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "seq_address", sequenceName = "seq_address")
    private Long id;
    private String city;
    private String zipCode;
    private String address;
    private Boolean checked;
    private String county;
    private String country;

    @ManyToOne
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(name = "fk_client_address")
    )
    private ClientUser clientUser;
}