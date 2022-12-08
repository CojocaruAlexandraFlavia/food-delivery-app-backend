package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Notification;
import com.example.fooddelivery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByOrder(Order q);
}