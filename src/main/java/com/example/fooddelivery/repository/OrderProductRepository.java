package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Order;
import com.example.fooddelivery.model.OrderProduct;
import com.example.fooddelivery.model.OrderProductId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OrderProductRepository  extends JpaRepository<OrderProduct, Long> {

    @Modifying
    @Transactional
    @Query("DELETE from OrderProduct o WHERE o.order.id = ?1 and o.product.id =?2")
    void deleteByOrderIdAndProductId(@NotNull Long orderId, Long productId);


}
