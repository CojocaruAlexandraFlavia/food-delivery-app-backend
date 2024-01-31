package com.example.fooddelivery.model;

import com.example.fooddelivery.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "seq_notification", sequenceName = "seq_notification")
    private Long id;
    private Boolean seen;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne
    @JoinColumn(
            name = "order_id",
            foreignKey = @ForeignKey(
                    name = "fk_order_notification"
            )
    )
    private Order order;

}
