package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Order;
import com.example.fooddelivery.model.OrderProduct;
import com.example.fooddelivery.model.OrderProductId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderProductRepository  extends JpaRepository<OrderProduct, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE from Restaurant q WHERE q.id = ?1")
    void deleteById(@NotNull Long id);


}
