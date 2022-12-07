package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Notification> notifications;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "order_products", joinColumns = {
            @JoinColumn(name = "order_id", referencedColumnName = "id",
            nullable = false, updatable = false)},
            inverseJoinColumns = {
            @JoinColumn(name = "product_id", referencedColumnName = "id",
            nullable = false, updatable = false)})
    private Set<Product> products = new HashSet<>();


    @ManyToOne
    private ClientUser clientUser;

    @ManyToOne
    private DeliveryUser deliveryUser;

    @ManyToOne
    private History history;

}
