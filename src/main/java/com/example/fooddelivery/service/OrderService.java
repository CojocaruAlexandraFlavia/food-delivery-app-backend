package com.example.fooddelivery.service;

import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.NotificationDto;
import com.example.fooddelivery.model.dto.OrderDto;
import com.example.fooddelivery.repository.NotificationRepository;
import com.example.fooddelivery.repository.OrderProductRepository;
import com.example.fooddelivery.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.fooddelivery.model.dto.RestaurantDto.entityToDto;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final OrderProductRepository orderProductRepository;

    private ClientUserService clientUserService;
    private DeliveryUserService deliveryUserService;
    private HistoryService historyService;

    @Autowired
    public OrderService(OrderRepository orderRepository, NotificationRepository notificationRepository,
                        OrderProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
        this.orderProductRepository = productRepository;
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<OrderProduct> findOrderProductById(Long id) {
        return orderProductRepository.findById(id);
    }

    public OrderDto saveOrder(@NotNull OrderDto orderDto){
        Order order = new Order();
        Optional<ClientUser> optionalClientUser = clientUserService.findClientUserById(orderDto.getClientUserId());
        Optional<DeliveryUser> optionalDeliveryUser = deliveryUserService.findDeliveryUserById(orderDto.getDeliveryUserId());
        Optional<History> optionalHistory = historyService.findHistoryById(orderDto.getHistoryId());

        if(optionalClientUser.isPresent() && optionalDeliveryUser.isPresent()){
            //save order
            order.setStatus(orderDto.getStatus());
            order.setNumber(orderDto.getNumber());
            order.setClientUser(optionalClientUser.get());
            order.setDeliveryUser(optionalDeliveryUser.get());
            order.setHistory(optionalHistory.get());
            order = orderRepository.save(order);

            //send notification
            Notification notification = new Notification();
            notification.setOrder(order);
            notificationRepository.save(notification);
            return OrderDto.entityToDto(order);
        }
        return null;
    }

    public NotificationDto seeNotification(Long notificationId){
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if(optionalNotification.isPresent()){
            Notification notification = optionalNotification.get();
            notification.setNotificationType(notification.getNotificationType());

            notification = notificationRepository.save(notification);
            return new NotificationDto();
        }
        return null;
    }

    public OrderDto updateOrder(Long orderId, OrderDto orderDto){
        Optional<Order> optionalOrder = findOrderById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            order.setStatus(orderDto.getStatus());
            order.setNumber(orderDto.getNumber());

            return OrderDto.entityToDto(orderRepository.save(order));
        }
        return null;
    }

    public boolean deleteOrder(Long orderId){
        if(orderId != null){
            Optional<Order> optionalOrder = findOrderById(orderId);
            if(optionalOrder.isPresent()){
                orderRepository.delete(optionalOrder.get());
                return true;
            }
        }
        return false;
    }

    public OrderDto addOrderProduct(Long orderId, Long orderProductId, int quantity){
        Optional<Order> optionalOrder = findOrderById(orderId);
        Optional<OrderProduct> optionalOrderProduct = findOrderProductById(orderProductId);

        if(optionalOrder.isPresent() && optionalOrderProduct.isPresent()){
            Order order = optionalOrder.get();
            OrderProduct op = new OrderProduct();
            op.setQuantity(quantity);
            orderProductRepository.save(op);
            return OrderDto.entityToDto(orderRepository.save(order));
        }
        return null;
    }

}
