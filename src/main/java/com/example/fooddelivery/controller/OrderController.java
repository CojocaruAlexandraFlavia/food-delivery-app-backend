package com.example.fooddelivery.controller;

import com.example.fooddelivery.model.Order;
import com.example.fooddelivery.model.dto.CheckOrderCountDto;
import com.example.fooddelivery.model.dto.NotificationDto;
import com.example.fooddelivery.model.dto.ViewCartDto;
import com.example.fooddelivery.model.dto.requests.AddOrderProductRequest;
import com.example.fooddelivery.model.dto.OrderDto;
import com.example.fooddelivery.model.dto.requests.UpdateCartProduct;
import com.example.fooddelivery.model.dto.user.DeliveryUserDto;
import com.example.fooddelivery.service.DeliveryUserService;
import com.example.fooddelivery.service.OrderService;
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
    private final DeliveryUserService deliveryUserService;

    @Autowired
    public OrderController(OrderService orderService, DeliveryUserService deliveryUserService) {
        this.orderService = orderService;
        this.deliveryUserService = deliveryUserService;
    }

//    @PostMapping("/save")
//    public ResponseEntity<OrderDto> saveOrder(@RequestBody OrderDto orderDto){
//        return ResponseEntity.of(Optional.of(orderService.saveOrder(orderDto)));
//    }

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

        OrderDto result = orderService.addOrderProduct(addOrderProductRequest);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @GetMapping("/cart/{clientId}")
    public ResponseEntity<ViewCartDto> viewCart(@PathVariable("clientId") Long id){
        ViewCartDto result = orderService.viewCart(id);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @PostMapping("/cart/decrease-quantity")
    public ResponseEntity<ViewCartDto> decreaseQuantityOfProduct(@RequestBody UpdateCartProduct updateCartProduct){
        ViewCartDto result = orderService.decreaseQuantityOfProduct(updateCartProduct.getProductId(), updateCartProduct.getClientId());
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


//    @GetMapping("/current-order/{clientId}")
//    public ResponseEntity<OrderDto> currentOrder(@PathVariable("clientId") Long id){
//        Optional<OrderDto> result = orderService.getCurrentOpenOrder(id);
//        if(result == null){
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(result, HttpStatus.OK);
//
//    }

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

    @GetMapping("/all-delivery-users")
    public ResponseEntity<List<DeliveryUserDto>> getALlDeliveryUsers() {
        List<DeliveryUserDto> deliveryUserDtoList = deliveryUserService.getAllDeliveryUsersDto();
        return new ResponseEntity<>(deliveryUserDtoList, HttpStatus.OK);
    }

    @DeleteMapping("/delete-delivery-user/{id}")
    public ResponseEntity<?> deleteDeliveryUser(@PathVariable("id") Long id) {
        deliveryUserService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update-delivery-user/{id}")
    public ResponseEntity<DeliveryUserDto> updateDeliveryUser(@PathVariable("id") Long id, @RequestBody DeliveryUserDto dto) {
        DeliveryUserDto result = deliveryUserService.updateDeliveryUser(id, dto);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-by-status/{status}")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(@PathVariable("status") String status) {
        List<OrderDto> result = orderService.getOrdersByStatus(status);
        if(result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping("/assign-deliver-to-order/{orderId}/{deliveryUserId}")
    public ResponseEntity<OrderDto> assignDeliverToOrder(@PathVariable("orderId") Long orderId,
                                                         @PathVariable("deliveryUserId") Long deliverId) {
        OrderDto result = orderService.assignDeliverToOrder(orderId, deliverId);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
