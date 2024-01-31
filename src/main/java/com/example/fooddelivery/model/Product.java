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
    @SequenceGenerator(name = "seq_product", sequenceName = "seq_product")
    private Long id;
    private Double price;
    private String name;
    private Double discount;
    private String ingredients;
    private boolean availability;

    @ManyToOne
    @JoinColumn(
            name = "category_id",
            foreignKey = @ForeignKey(name = "fk_category_product")
    )
    private Category category;

    @ManyToOne
    @JoinColumn(
            name = "restaurant_id",
            foreignKey = @ForeignKey(name = "fk_restaurant_product")
    )
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
