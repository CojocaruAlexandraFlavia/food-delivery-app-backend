package com.example.fooddelivery.model;

import com.example.fooddelivery.enums.OrderStatus;
import com.example.fooddelivery.enums.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "seq_order", sequenceName = "seq_order")
    private Long id;
    private Integer orderNumber;
    private Double value;
    private Double deliveryTax;
    private LocalDateTime dateTime;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> products;

    @ManyToOne
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(name = "fk_client_order")
    )
    private ClientUser clientUser;

    @ManyToOne
    @JoinColumn(
            name = "delivery_user_id",
            foreignKey = @ForeignKey(name = "fk_delivery_user_order")
    )
    private DeliveryUser deliveryUser;

    @ManyToOne
    @JoinColumn(
            name = "delivery_address_id",
            foreignKey = @ForeignKey(name = "fk_delivery_address_order")
    )
    private UserAddress deliveryAddress;

}
