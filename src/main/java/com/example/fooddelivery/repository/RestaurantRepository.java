package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Restaurant;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE from Restaurant q WHERE q.id = ?1")
    void deleteById(@NotNull Long id);

    Optional<Restaurant> findByName(Restaurant name);
}