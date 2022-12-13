package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Product;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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

    public static @NotNull ProductDto entityToDto(@NotNull Product product) {
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setIngredients(product.getIngredients());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setRestaurantId(product.getRestaurant().getId());
        return dto;
    }

}
