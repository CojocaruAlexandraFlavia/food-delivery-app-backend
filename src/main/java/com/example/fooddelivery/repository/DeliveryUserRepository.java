package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.DeliveryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeliveryUserRepository extends JpaRepository<DeliveryUser, Long> {
}