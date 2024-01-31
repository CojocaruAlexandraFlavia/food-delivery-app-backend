package com.example.fooddelivery.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@IdClass(OrderProductId.class)
public class OrderProduct {

    @Id
    @ManyToOne
    @JoinColumn(
            name = "order_id",
            foreignKey = @ForeignKey(name = "fk_order_m2m"),
            referencedColumnName = "id"
    )
    private Order order;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "product_id",
            foreignKey = @ForeignKey(name = "fk_product_m2m"),
            referencedColumnName = "id"
    )
    private Product product;

    private int quantity;


}
