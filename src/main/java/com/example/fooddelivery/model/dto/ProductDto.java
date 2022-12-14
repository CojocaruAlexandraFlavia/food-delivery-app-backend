package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class ProductDto {

    private Double price;
    private String name;
    private Double discount;
    private String ingredients;

    private Long categoryId;
    private String categoryName;

    private Long restaurantId;
    private String restaurantName;

    private Long clientUserId;

    private List<OrderProductDto> orders;


    public static @NotNull ProductDto entityToDto(@NotNull Product product) {
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setIngredients(product.getIngredients());

        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());

        dto.setRestaurantId(product.getRestaurant().getId());
        dto.setRestaurantName(product.getRestaurant().getName());

        if(product.getOrders() !=null){
            dto.setOrders(product.getOrders().stream().map(OrderProductDto::entityToDto).collect(toList()));
        }
        return dto;
    }
}
