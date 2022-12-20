package com.example.fooddelivery.model.dto;


import com.example.fooddelivery.model.Order;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class CheckOrderCountDto {
    private Long numberOfOrders;
    private double totalCount;

    public static @NotNull CheckOrderCountDto entityToDto(Long numberOfOrders, Long totalCount) {
        CheckOrderCountDto checkOrderCountDto = new CheckOrderCountDto();
        checkOrderCountDto.setNumberOfOrders(numberOfOrders);
        checkOrderCountDto.setTotalCount(totalCount);
        return checkOrderCountDto;
    }
}
