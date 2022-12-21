package com.example.fooddelivery.model;

import com.example.fooddelivery.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "placed_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long number;
    private Double value;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> products;

    @ManyToOne
    private ClientUser clientUser;

    @ManyToOne
    private DeliveryUser deliveryUser;

    public double getTotalPrice(){
        double price = 0.0;
        for (OrderProduct p:products) {
            price += (p.getProduct().getPrice() - ((p.getProduct().getDiscount()/100))* p.getProduct().getPrice()) * (p.getQuantity());
        }
        return price;
    }

}
