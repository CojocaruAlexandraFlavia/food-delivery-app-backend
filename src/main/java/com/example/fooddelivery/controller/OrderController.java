package com.example.fooddelivery.controller;

import com.example.fooddelivery.enums.NotificationType;
import com.example.fooddelivery.model.Notification;
import com.example.fooddelivery.model.Order;
import com.example.fooddelivery.model.dto.CheckOrderCountDto;
import com.example.fooddelivery.model.dto.requests.AddOrderProductRequest;
import com.example.fooddelivery.model.dto.OrderDto;
import com.example.fooddelivery.repository.NotificationRepository;
import com.example.fooddelivery.service.OrderService;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@Transactional
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public OrderController(OrderService orderService, NotificationRepository notificationRepository) {
        this.orderService = orderService;
        this.notificationRepository = notificationRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<OrderDto> saveOrder(@RequestBody OrderDto orderDto){
        return ResponseEntity.of(Optional.of(orderService.saveOrder(orderDto)));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<OrderDto> findOrderById(@PathVariable("id") Long id){
        Optional<Order> optionalOrder = orderService.findOrderById(id);
        if(optionalOrder.isPresent()){
            OrderDto orderDto = OrderDto.entityToDto(optionalOrder.get());
            return new ResponseEntity<>(orderDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable("id") Long id){
        boolean result = orderService.deleteOrder(id);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/change-status/{orderId}/{status}")
    public ResponseEntity<OrderDto> changeOrderStatus(@PathVariable("orderId") Long id,
                                                      @PathVariable("status") String newStatus){
        OrderDto result = orderService.updateOrder(id, newStatus);
        Notification notification = new Notification();
        Optional<Order> o = orderService.findOrderById(id);
        if(o.isPresent()){
            notification.setOrder(o.get());
            notification.setNotificationType(NotificationType.valueOf(newStatus));
            notificationRepository.save(notification);
        }

        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/add-products")
    public ResponseEntity<OrderDto> addOrderProduct(@RequestBody AddOrderProductRequest addOrderProductRequest){
        OrderDto result = orderService.addOrderProduct(addOrderProductRequest.getOrderId(),
                addOrderProductRequest.getOrderProductId(), addOrderProductRequest.getQuantity());
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
    @GetMapping("/check-total-count")
    public ResponseEntity<CheckOrderCountDto> checkTotalCount(){
        Long orders = (long) orderService.getAll().size();
        double price = orderService.getTotalCounts();
        CheckOrderCountDto checkOrderCountDto =  new CheckOrderCountDto();
        checkOrderCountDto.setTotalCount(price);
        checkOrderCountDto.setNumberOfOrders(orders);
        return new ResponseEntity<>(checkOrderCountDto, HttpStatus.OK);

    }

}
