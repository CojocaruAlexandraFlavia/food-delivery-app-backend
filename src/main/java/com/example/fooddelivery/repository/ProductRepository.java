package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Category;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.model.Restaurant;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository  extends JpaRepository<Product, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE from Product q WHERE q.id = ?1")
    void deleteById(@NotNull Long id);

    List<Product> findByCategory(Category q);
    List<Restaurant> findByRestaurant(Restaurant q);

    Optional<Product> findByName(Product name);
}
