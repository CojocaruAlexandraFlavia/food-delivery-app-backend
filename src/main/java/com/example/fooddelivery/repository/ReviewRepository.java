package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Review;
import com.example.fooddelivery.model.Restaurant;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE from Review q WHERE q.id = ?1")
    void deleteById(@NotNull Long id);

}
