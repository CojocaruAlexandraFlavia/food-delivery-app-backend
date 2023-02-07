package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Category;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class CategoryDto {

    private Long id;
    private String name;
    private List<ProductDto> products;

    public static @NotNull CategoryDto entityToDto(@NotNull Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setProducts(category.getProducts().stream().map(ProductDto::entityToDto).collect(toList()));
        return dto;
    }

}
