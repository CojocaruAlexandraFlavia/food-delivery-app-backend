package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.NotificationType;
import com.example.fooddelivery.enums.OrderStatus;
import com.example.fooddelivery.enums.PaymentType;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.*;
import com.example.fooddelivery.model.dto.requests.AddOrderProductRequest;
import com.example.fooddelivery.model.dto.requests.SendOrder;
import com.example.fooddelivery.repository.NotificationRepository;
import com.example.fooddelivery.repository.OrderProductRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.repository.UserAddressRepository;
import org.apache.catalina.User;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final OrderProductRepository orderProductRepository;

    private final ProductService productService;

    private final ClientUserService clientUserService;
    private final DeliveryUserService deliveryUserService;
    private final UserAddressRepository addressRepository;
    private final RestaurantService restaurantService;

    @Autowired
    public OrderService(OrderRepository orderRepository, NotificationRepository notificationRepository,
                        OrderProductRepository productRepository, ProductService productService,
                        ClientUserService clientUserService, DeliveryUserService deliveryUserService,
                        UserAddressRepository addressRepository, RestaurantService restaurantService) {
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
        this.orderProductRepository = productRepository;
        this.productService = productService;
        this.clientUserService = clientUserService;
        this.deliveryUserService = deliveryUserService;
        this.addressRepository = addressRepository;
        this.restaurantService = restaurantService;
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<OrderProduct> findOrderProductById(Long id) {
        return orderProductRepository.findById(id);
    }


    public ViewCartDto viewCart(Long clientId){
        Optional<ClientUser> optionalClientUser = clientUserService.findClientUserById(clientId);
        ViewCartDto viewCartDto = new ViewCartDto();
        viewCartDto.setClientUserId(clientId);
        if(optionalClientUser.isPresent()){
            Optional<Order> order = getCurrentOpenOrder(clientId);
            if(order.isPresent()){
                Order viewOrder = order.get();
                viewCartDto.setOrderId(viewOrder.getId());
                viewCartDto.setNumber(viewOrder.getNumber());
                List<OrderProductDto> orderProducts = viewOrder.getProducts().stream().map(OrderProductDto::entityToDto).collect(toList());
                viewCartDto.setProducts(orderProducts);
                viewCartDto.setValue(order.get().getValue());
                viewCartDto.setDeliveryTax(order.get().getDeliveryTax());
                orderRepository.save(viewOrder);
                return viewCartDto;
            }
        }
        return null;

    }

    public ViewCartDto decreaseQuantityOfProduct(Long productId, Long clientId){
        Optional<Product> orderProduct = productService.findProductById(productId);
        Optional<Order> order = getCurrentOpenOrder(clientId);

        if(orderProduct.isPresent() && order.isPresent()){
            Product product = orderProduct.get();
            Order updatedOrder = order.get();
            List<OrderProduct> orderProducts = updatedOrder.getProducts();
            OrderProduct orderProductUpdated = new OrderProduct();
            Boolean isFound = false;
            for (OrderProduct orderProduct1: orderProducts) {
                if (orderProduct1.getProduct().getId().equals(productId)) {
                    if (orderProduct1.getQuantity() - 1 == 0) {
                        System.out.println("da");
                        // deleteProduct(product.getId(), clientId);
                        orderProductRepository.deleteByOrderIdAndProductId(updatedOrder.getId(), product.getId());

                    } else {
                        orderProductUpdated.setOrder(updatedOrder);
                        orderProductUpdated.setProduct(product);
                        orderProductUpdated.setQuantity(orderProduct1.getQuantity() - 1);
                        orderProductUpdated = orderProductRepository.save(orderProductUpdated);
                    }
                    isFound = true;
                }
            }
            if(isFound){
                double productPriceWithDiscountApplied = product.getPrice() -
                        product.getDiscount() / 100 * product.getPrice();
                updatedOrder.setValue(updatedOrder.getValue() - productPriceWithDiscountApplied);
                orderRepository.save(updatedOrder);
            }


            //updateOrderPrice(updatedOrder);

            return viewCart(order.get().getClientUser().getId());
        }
        return null;

    }


    public ViewCartDto increaseQuantityOfProduct(Long productId, Long clientId){
        Optional<Product> orderProduct = productService.findProductById(productId);
        Optional<Order> order = getCurrentOpenOrder(clientId);

        if(orderProduct.isPresent() && order.isPresent()){
            Product product = orderProduct.get();
            Order updatedOrder = order.get();
            List<OrderProduct> orderProducts = updatedOrder.getProducts();
            OrderProduct orderProductUpdated = new OrderProduct();
            Boolean isFound = false;
            for (OrderProduct orderProduct1: orderProducts) {
                if (orderProduct1.getProduct().getId().equals(productId)) {
                    orderProductUpdated.setOrder(updatedOrder);
                    orderProductUpdated.setProduct(product);
                    orderProductUpdated.setQuantity(orderProduct1.getQuantity() + 1);
                    orderProductUpdated = orderProductRepository.save(orderProductUpdated);
                    isFound = true;
                }
            }

            if(isFound){
                double productPriceWithDiscountApplied = product.getPrice() -
                        product.getDiscount() / 100 * product.getPrice();
                updatedOrder.setValue(updatedOrder.getValue() + productPriceWithDiscountApplied);
                updatedOrder = orderRepository.save(updatedOrder);
            }
            //updateOrderPrice(updatedOrder);
            return viewCart(order.get().getClientUser().getId());
        }
        return null;

    }

    public void deleteProduct(Long productId, Long clientId){
        Optional<Product> orderProduct = productService.findProductById(productId);
        Optional<Order> order = getCurrentOpenOrder(clientId);

        if(orderProduct.isPresent() && order.isPresent()){
            Product product = orderProduct.get();
            Order updatedOrder = order.get();
            List<OrderProduct> orderProducts = updatedOrder.getProducts();
            boolean isFound = false;
            int quantity = 0;
            for (OrderProduct orderProduct1: orderProducts) {
                if (orderProduct1.getProduct().getId().equals(productId)) {
                    orderProductRepository.deleteByOrderIdAndProductId(updatedOrder.getId(), product.getId());
                    quantity = orderProduct1.getQuantity();
                    isFound = true;
                }
            }

            if(isFound){
                double productPriceWithDiscountApplied = product.getPrice() -
                        product.getDiscount() / 100 * product.getPrice();
                updatedOrder.setValue(updatedOrder.getValue() - (productPriceWithDiscountApplied * quantity));
                orderRepository.save(updatedOrder);
            }

        }

    }
    public void updateOrderPrice(Order order){
        List<OrderProduct> products = order.getProducts();
        Double value = 0.0;
        for (OrderProduct orderProduct1: products) {
            Product product = orderProduct1.getProduct();
            double productPriceWithDiscountApplied = product.getPrice() -
                    product.getDiscount() / 100 * product.getPrice();
            Double productValue = productPriceWithDiscountApplied * orderProduct1.getQuantity();
            value = value + productValue;

        }
        order.setValue(value);
        order.setTotalPrice(value + order.getDeliveryTax());
        orderRepository.save(order);

    }

    public OrderDto updateOrderAddress(SendOrder sendOrder){
        Long clientId = sendOrder.getClientId();
        Long addressId = sendOrder.getAddressId();
        System.out.println(clientId + " " +  addressId);
        Optional<Order> order = getCurrentOpenOrder(clientId);
        if(order.isPresent()) {
            System.out.println("order is presnt");
            Order currentOrder = order.get();
            Optional<UserAddress> address = addressRepository.findById(addressId);
            if (address.isPresent()) {
                System.out.println("is present");
                UserAddress userAddress = address.get();
                currentOrder.setDeliveryAddress(userAddress);
                return OrderDto.entityToDto(orderRepository.save(currentOrder));
            }
        }
        return null;
    }

    public OrderDto sendOrder(SendOrder sendOrder){
        Long clientId = sendOrder.getClientId();
        Optional<Order> order = getCurrentOpenOrder(clientId);
        if(order.isPresent()){
            Order currentOrder = order.get();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            currentOrder.setDateTime(LocalDateTime.from(dateTimeFormatter.parse(LocalDateTime.now().format(dateTimeFormatter))));
            currentOrder.setStatus(OrderStatus.RECEIVED);
            Notification notification = new Notification();
            notification.setOrder(currentOrder);
            notification.setType(NotificationType.ORDER_RECEIVED);
            notification.setSeen(false);
            notificationRepository.save(notification);
            return OrderDto.entityToDto(orderRepository.save(currentOrder));

        }
        return null;
    }


    public NotificationDto seeNotification(Long notificationId){
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if(optionalNotification.isPresent()){
            Notification notification = optionalNotification.get();
            notification.setSeen(true);
            notification = notificationRepository.save(notification);
            return NotificationDto.entityToDto(notification);
        }
        return null;
    }

    public OrderDto updateOrderStatus(Long orderId, String newStatus) {
        Optional<Order> optionalOrder = findOrderById(orderId);
        if(optionalOrder.isPresent() && EnumUtils.isValidEnum(OrderStatus.class, newStatus)) {
            Order order = optionalOrder.get();
            OrderStatus orderStatus = OrderStatus.valueOf(newStatus);
            order.setStatus(orderStatus);

            //send notification about order status
            Notification notification = new Notification();
            notification.setSeen(false);
            notification.setOrder(order);
            if(orderStatus.equals(OrderStatus.PICKED_UP)) {
                notification.setType(NotificationType.ORDER_PICKED_UP);
            } else if (orderStatus.equals(OrderStatus.ON_THE_WAY)) {
                notification.setType(NotificationType.ORDER_ON_THE_WAY);
            } else {
                notification.setType(NotificationType.ORDER_DELIVERED);
            }
            notificationRepository.save(notification);
            return OrderDto.entityToDto(orderRepository.save(order));
        }
        return null;
    }

    public boolean deleteOrder(Long orderId) {
        if(orderId != null){
            Optional<Order> optionalOrder = findOrderById(orderId);
            if(optionalOrder.isPresent()){
                orderRepository.delete(optionalOrder.get());
                return true;
            }
        }
        return false;
    }
    //or create new open order
    public Optional<Order> getCurrentOpenOrder(Long clientId){

        List<Order> orders = orderRepository.findByClientUserId(clientId);

        List<Order> expiredOpenOrders = orders.stream().filter(matchOrder ->
                matchOrder.getDateTime().toLocalDate().isBefore(LocalDateTime.now().toLocalDate()) && matchOrder.getStatus().equals(OrderStatus.OPEN)).collect(Collectors.toList());
        for (Order o: expiredOpenOrders) {
            for(OrderProduct orderProduct : o.getProducts()){
                orderProductRepository.delete(orderProduct);
            }
            deleteOrder(o.getId());
        }

        Optional<Order> order = orders.stream().filter(matchOrder ->
                matchOrder.getDateTime().toString().substring(0,10).equals(LocalDateTime.now().toString().substring(0,10))).filter(matchOrder ->
                matchOrder.getStatus().equals(OrderStatus.OPEN)).findAny();
        if(order.isPresent()){
                System.out.println("are si OPEN");
                return order;
        }

       return Optional.empty();
    }


    public Order createOpenOrder(Long clientId, Long restaurantId){
        Optional<ClientUser> clientUser = clientUserService.findClientUserById(clientId);
        Optional<Restaurant> restaurant = restaurantService.findRestaurantById(restaurantId);
        if(clientUser.isPresent() && restaurant.isPresent()){
            Order order = new Order();

            //set order number
            Order lastSavedOrder = orderRepository.findFirstByOrderByIdDesc();
            if(lastSavedOrder != null) {
                order.setNumber(lastSavedOrder.getNumber() + 1L);
            } else {
                order.setNumber(1L);
            }
            //save order
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            order.setDateTime(LocalDateTime.from(dateTimeFormatter.parse(LocalDateTime.now().format(dateTimeFormatter))));
            order.setStatus(OrderStatus.OPEN);
            order.setClientUser(clientUser.get());

            order.setDeliveryTax(restaurant.get().getDeliveryTax());
            order.setPaymentType(PaymentType.CARD_ONLINE);
            order.setValue(0.0);
            order.setNotifications(new ArrayList<>());
            order.setProducts(new ArrayList<>());
            return orderRepository.save(order);
        }
        return null;
    }





    public OrderDto addOrderProduct(AddOrderProductRequest addOrderProductRequest){

        Long clientId = addOrderProductRequest.getClientId();
        Long productId = addOrderProductRequest.getProductId();
        int quantity = addOrderProductRequest.getQuantity();

        Optional<Order> optionalOrder = getCurrentOpenOrder(clientId);
        OrderProduct orderProduct = new OrderProduct();

        Optional<Product> productOptional = productService.findProductById(productId);
        Order order = new Order();


        if(productOptional.isPresent()){
            Product product = productOptional.get();
            Long restaurantId = productOptional.get().getRestaurant().getId();
            if(optionalOrder.isPresent()){
                System.out.println("order pre existent");
                order = optionalOrder.get();
                Boolean found = false;
                for (OrderProduct orderProduct1: order.getProducts()) {
                    if(orderProduct1.getProduct().getId().equals(productId)){
                        orderProduct.setOrder(order);
                        orderProduct.setProduct(product);
                        orderProduct.setQuantity(orderProduct1.getQuantity() + quantity);
                        orderProduct = orderProductRepository.save(orderProduct);
                        found = true;
                    }
                }
                if(!found){
                    orderProduct.setOrder(order);
                    orderProduct.setQuantity(quantity);
                    orderProduct.setProduct(product);
                    orderProduct = orderProductRepository.save(orderProduct);
                    List<OrderProduct> orderProducts = order.getProducts();
                    orderProducts.add(orderProduct);
                    order.setProducts(orderProducts);

                }

            }else {
                order = createOpenOrder(clientId, restaurantId);
                orderProduct.setOrder(order);
                orderProduct.setQuantity(quantity);
                orderProduct.setProduct(product);
                orderProduct = orderProductRepository.save(orderProduct);
                List<OrderProduct> orderProducts = new ArrayList<>();
                orderProducts.add(orderProduct);
                order.setProducts(orderProducts);

            }

            Double oldValue =  order.getValue();
            double productPriceWithDiscountApplied = product.getPrice() -
                        product.getDiscount() / 100 * product.getPrice();
            Double updatedValue = oldValue + (productPriceWithDiscountApplied * quantity);
            order.setValue(updatedValue);


            order = orderRepository.save(order);
            System.out.println(order.getDeliveryTax());
            return OrderDto.entityToDto(order);

        }

         return null;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public double getTotalCounts() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().mapToDouble(Order::getValue).sum();
    }

    public CheckOrderCountDto checkTotalCount() {
        Long orders = (long) getAll().size();
        double price = getTotalCounts();
        CheckOrderCountDto checkOrderCountDto =  new CheckOrderCountDto();
        checkOrderCountDto.setTotalCount(price);
        checkOrderCountDto.setNumberOfOrders(orders);
        return checkOrderCountDto;
    }

    public List<OrderDto> getOrdersByStatus(String status) {
        if(EnumUtils.isValidEnumIgnoreCase(OrderStatus.class, status)) {
            List<Order> filteredOrders = orderRepository.findByStatus(OrderStatus.valueOf(status.toUpperCase()));
            return filteredOrders.stream().map(OrderDto::entityToDto).collect(toList());
        }
        return new ArrayList<>();
    }

    public OrderDto assignDeliverToOrder(Long orderId, Long deliverId) {
        Optional<Order> optionalOrder = findOrderById(orderId);
        Optional<DeliveryUser> optionalDeliveryUser = deliveryUserService.findDeliveryUserById(deliverId);

        if(optionalOrder.isPresent() && optionalDeliveryUser.isPresent()) {
            Order order = optionalOrder.get();
            order.setDeliveryUser(optionalDeliveryUser.get());
            order = orderRepository.save(order);
            return OrderDto.entityToDto(order);
        }

        return null;
    }
}
