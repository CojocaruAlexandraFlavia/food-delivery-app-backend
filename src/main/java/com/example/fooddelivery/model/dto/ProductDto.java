package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    private Long id;
    private Double price;
    private String name;
    private Double discount;
    private String ingredients;
    private boolean availability;

    private Long categoryId;
    private String categoryName;

    private Long restaurantId;
    private String restaurantName;

    public static @NotNull ProductDto entityToDto(@NotNull Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setIngredients(product.getIngredients());
        dto.setAvailability(product.isAvailability());
        dto.setCategoryName(product.getCategory().getName());

        if(product.getRestaurant() != null) {
            dto.setRestaurantName(product.getRestaurant().getName());
        }
        return dto;
    }
}
