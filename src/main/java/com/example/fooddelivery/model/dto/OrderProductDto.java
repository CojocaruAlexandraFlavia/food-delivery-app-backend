package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.OrderProduct;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class OrderProductDto {

    private ProductDto productDto;
    private int quantity;

    public static @NotNull OrderProductDto entityToDto(@NotNull OrderProduct orderProduct) {
        OrderProductDto dto = new OrderProductDto();
        dto.setProductDto(ProductDto.entityToDto(orderProduct.getProduct()));
        dto.setQuantity(orderProduct.getQuantity());
        return dto;
    }
}
