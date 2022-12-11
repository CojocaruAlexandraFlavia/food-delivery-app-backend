package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Notification;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class NotificationDto {

    private boolean seen;
    private String notificationType;
    private Long orderId;

    public static @NotNull NotificationDto entityToDto(@NotNull Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setSeen(notification.getSeen());
        dto.setNotificationType(notification.getNotificationType());
        dto.setOrderId(notification.getOrder().getId());
        return dto;
    }

}
