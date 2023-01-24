package com.example.fooddelivery.controller;

import com.example.fooddelivery.model.Order;
import com.example.fooddelivery.model.dto.CheckOrderCountDto;
import com.example.fooddelivery.model.dto.NotificationDto;
import com.example.fooddelivery.model.dto.requests.AddOrderProductRequest;
import com.example.fooddelivery.model.dto.OrderDto;
import com.example.fooddelivery.model.dto.user.DeliveryUserDto;
import com.example.fooddelivery.service.DeliveryUserService;
import com.example.fooddelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Transactional
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final DeliveryUserService deliveryUserService;

    @Autowired
    public OrderController(OrderService orderService, DeliveryUserService deliveryUserService) {
        this.orderService = orderService;
        this.deliveryUserService = deliveryUserService;
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
        OrderDto result = orderService.updateOrderStatus(id, newStatus);
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

    @PatchMapping("/see-notification/{id}")
    public ResponseEntity<NotificationDto> seeNotification(@PathVariable("id") Long id) {
        NotificationDto result = orderService.seeNotification(id);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/check-total-count")
    public ResponseEntity<CheckOrderCountDto> checkTotalCount(){
        CheckOrderCountDto result = orderService.checkTotalCount();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/delivery-user/{id}")
    public ResponseEntity<DeliveryUserDto> getDeliveryUser(@PathVariable("id") Long id) {
        DeliveryUserDto result = deliveryUserService.findByIdDto(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
