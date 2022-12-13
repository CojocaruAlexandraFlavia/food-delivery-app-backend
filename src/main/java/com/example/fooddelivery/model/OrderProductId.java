package com.example.fooddelivery.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class OrderProductId implements Serializable {

    private Long order;
    private Long product;

}
