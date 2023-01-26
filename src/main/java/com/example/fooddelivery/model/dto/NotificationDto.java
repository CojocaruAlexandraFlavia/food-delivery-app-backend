package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Notification;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class NotificationDto {

    private Long id;
    private boolean seen;
    private String type;
    private Long orderId;

    public static @NotNull NotificationDto entityToDto(@NotNull Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setSeen(notification.getSeen());
        dto.setType(notification.getType().toString());
        dto.setOrderId(notification.getOrder().getId());
        return dto;
    }

}
