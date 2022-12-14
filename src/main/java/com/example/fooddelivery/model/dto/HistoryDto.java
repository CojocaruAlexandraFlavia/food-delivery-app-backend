package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.History;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class HistoryDto {

    private List<OrderDto> orders;
    private Long clientUserId;
    private Long deliveryUserId;

    public static @NotNull HistoryDto entityToDto(@NotNull History history) {
        HistoryDto dto = new HistoryDto();

        dto.setClientUserId(history.getClientUser().getId());
        dto.setDeliveryUserId(history.getDeliveryUser().getId());
        return dto;
    }

}
