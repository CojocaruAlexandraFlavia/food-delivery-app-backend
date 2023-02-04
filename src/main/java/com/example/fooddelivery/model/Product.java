package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private String name;
    private Double discount;
    private String ingredients;
    private boolean availability;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany(mappedBy = "products")
    private Set<ClientUser> clientUsers;

    @OneToMany(mappedBy = "product")
    private List<OrderProduct> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return getId() != null ? getId().equals(product.getId()) : product.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
