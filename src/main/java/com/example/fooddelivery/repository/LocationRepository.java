package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Location;
import com.example.fooddelivery.model.Restaurant;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository  extends JpaRepository<Location, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE from Location q WHERE q.id = ?1")
    void deleteById(@NotNull Long id);

    List<Location> findByRestaurant(Restaurant q);

    Optional<Location> findByCity(Location city);
}
