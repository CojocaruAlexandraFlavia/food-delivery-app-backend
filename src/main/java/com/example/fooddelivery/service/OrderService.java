package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.NotificationType;
import com.example.fooddelivery.enums.OrderStatus;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.NotificationDto;
import com.example.fooddelivery.model.dto.OrderDto;
import com.example.fooddelivery.model.dto.ProductDto;
import com.example.fooddelivery.repository.NotificationRepository;
import com.example.fooddelivery.repository.OrderProductRepository;
import com.example.fooddelivery.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final OrderProductRepository orderProductRepository;

    private final ProductService productService;

    private final ClientUserService clientUserService;
    private final DeliveryUserService deliveryUserService;

    @Autowired
    public OrderService(OrderRepository orderRepository, NotificationRepository notificationRepository,
                        OrderProductRepository productRepository, ProductService productService, ClientUserService clientUserService, DeliveryUserService deliveryUserService) {
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
        this.orderProductRepository = productRepository;
        this.productService = productService;
        this.clientUserService = clientUserService;
        this.deliveryUserService = deliveryUserService;
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

        if(optionalClientUser.isPresent() && optionalDeliveryUser.isPresent()){

            //set order number
            Order lastSavedOrder = orderRepository.findFirstByOrderByIdDesc();
            if(lastSavedOrder != null) {
                order.setNumber(lastSavedOrder.getNumber() + 1L);
            } else {
                order.setNumber(1L);
            }

            //save order
            order.setStatus(OrderStatus.RECEIVED);
            order.setClientUser(optionalClientUser.get());
            order.setDeliveryUser(optionalDeliveryUser.get());
            Order savedOrder = orderRepository.save(order);

            //save order products
            List<OrderProduct> orderProducts = new ArrayList<>();
            AtomicReference<Double> value = new AtomicReference<>(0.0);
            orderDto.getProducts().forEach(orderProductDto -> {
                ProductDto productDto = orderProductDto.getProductDto();
                Optional<Product> optionalProduct = productService.findProductById(productDto.getId());
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setQuantity(orderProductDto.getQuantity());
                orderProduct.setOrder(savedOrder);
                orderProduct.setProduct(optionalProduct.orElseThrow(EntityNotFoundException::new));
                orderProducts.add(orderProduct);
                double productPriceWithDiscountApplied = optionalProduct.get().getPrice() -
                        optionalProduct.get().getDiscount() / 100 * optionalProduct.get().getPrice();
                value.updateAndGet(v -> v + productPriceWithDiscountApplied * orderProductDto.getQuantity());
            });
            List<OrderProduct> savedOrderProducts = orderProductRepository.saveAll(orderProducts);
            savedOrder.setProducts(savedOrderProducts);
            savedOrder.setValue(value.get() + orderDto.getDeliveryTax());

            //send notification
            Notification notification = new Notification();
            notification.setOrder(savedOrder);
            notification.setNotificationType(NotificationType.ORDER_RECEIVED);
            notification.setSeen(false);
            notificationRepository.save(notification);
            return OrderDto.entityToDto(orderRepository.save(savedOrder));
        }
        return null;
    }

    public NotificationDto seeNotification(Long notificationId){
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if(optionalNotification.isPresent()){
            Notification notification = optionalNotification.get();
            notification.setNotificationType(notification.getNotificationType());
            notification = notificationRepository.save(notification);
            return NotificationDto.entityToDto(notification);
        }
        return null;
    }

    public OrderDto updateOrder(Long orderId, String newStatus){
        Optional<Order> optionalOrder = findOrderById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            order.setStatus(OrderStatus.valueOf(newStatus));
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
