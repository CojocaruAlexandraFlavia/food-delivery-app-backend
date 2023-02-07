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
    private Long numberOfProducts;
    private Long numberOfCities;
    private Long numberOfRestaurants;

    public static @NotNull CheckOrderCountDto entityToDto(Long numberOfOrders, Long totalCount,
                                                          Long numberOfProducts, Long numberOfCities,
                                                          Long numberOfRestaurants) {
        CheckOrderCountDto checkOrderCountDto = new CheckOrderCountDto();
        checkOrderCountDto.setNumberOfOrders(numberOfOrders);
        checkOrderCountDto.setTotalCount(totalCount);
        checkOrderCountDto.setNumberOfProducts(numberOfProducts);
        checkOrderCountDto.setNumberOfCities(numberOfCities);
        checkOrderCountDto.setNumberOfRestaurants(numberOfRestaurants);
        return checkOrderCountDto;
    }
}
