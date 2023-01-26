package com.example.fooddelivery.model.dto;

import com.example.fooddelivery.model.Review;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ReviewDto {

    private int stars;
    private String comment;
    private Long clientUserId;
    private Long restaurantId;

    private String clientFirstName;
    private String clientLastName;
    private String restaurantName;

    public static @NotNull ReviewDto entityToDto(@NotNull Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setComment(review.getComment());
        dto.setStars(review.getStars());
        dto.setClientUserId(review.getClientUser().getId());
        dto.setRestaurantId(review.getRestaurant().getId());
        dto.setClientLastName(review.getClientUser().getLastName());
        dto.setClientFirstName(review.getClientUser().getFirstName());
        dto.setRestaurantName(review.getRestaurant().getName());
        return dto;
    }
}
