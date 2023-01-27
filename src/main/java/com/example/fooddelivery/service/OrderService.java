package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.NotificationType;
import com.example.fooddelivery.enums.OrderStatus;
import com.example.fooddelivery.enums.PaymentType;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.*;
import com.example.fooddelivery.repository.NotificationRepository;
import com.example.fooddelivery.repository.OrderProductRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.repository.UserAddressRepository;
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
                viewCartDto.setId(viewOrder.getId());
                viewCartDto.setNumber(viewOrder.getNumber());
                List<OrderProductDto> orderProducts = viewOrder.getProducts().stream().map(OrderProductDto::entityToDto).collect(toList());
                viewCartDto.setProducts(orderProducts);
                AtomicReference<Double> value = new AtomicReference<>(0.0);
                orderProducts.forEach(orderProductDto -> {
                    ProductDto productDto = orderProductDto.getProductDto();
                    Optional<Product> optionalProduct = productService.findProductById(productDto.getId());
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setQuantity(orderProductDto.getQuantity());
                    orderProduct.setOrder(viewOrder);
                    orderProduct.setProduct(optionalProduct.orElseThrow(EntityNotFoundException::new));
                    double productPriceWithDiscountApplied = optionalProduct.get().getPrice() -
                            optionalProduct.get().getDiscount() / 100 * optionalProduct.get().getPrice();
                    value.updateAndGet(v -> v + productPriceWithDiscountApplied * orderProductDto.getQuantity());
                viewOrder.setValue(value.get());

                });
                viewCartDto.setValue(value.get());
                viewCartDto.setDeliveryTax(order.get().getDeliveryTax());
                orderRepository.save(viewOrder);
                return viewCartDto;
            }
        }
        return null;

    }


    public OrderDto sendOrder(@NotNull Long clientId){
        Optional<Order> order = getCurrentOpenOrder(clientId);
        if(order.isPresent()){
            Order currentOrder = order.get();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            currentOrder.setDateTime(LocalDateTime.from(dateTimeFormatter.parse(LocalDateTime.now().format(dateTimeFormatter))));
            List<UserAddress> addresses = addressRepository.findByClientUserId(clientId);
            Optional<UserAddress> userAddress = addresses.stream().filter(checkedAddress ->
                    checkedAddress.getChecked().equals(true)).findAny();
            if(userAddress.isPresent()) {
                currentOrder.setDeliveryAddress(userAddress.get());
                currentOrder.setStatus(OrderStatus.RECEIVED);

                Notification notification = new Notification();
                notification.setOrder(currentOrder);
                notification.setType(NotificationType.ORDER_RECEIVED);
                notification.setSeen(false);
                notificationRepository.save(notification);
                return OrderDto.entityToDto(orderRepository.save(currentOrder));

            }

        }
        return null;
    }


    public OrderDto saveOrder(@NotNull OrderDto orderDto){
        Order order = new Order();
        Optional<ClientUser> optionalClientUser = clientUserService.findClientUserById(orderDto.getClientUserId());
        Optional<DeliveryUser> optionalDeliveryUser = deliveryUserService.findDeliveryUserById(orderDto.getDeliveryUserId());
        if(optionalClientUser.isPresent() && optionalDeliveryUser.isPresent()){

            //set delivery address
            UserAddressDto deliveryAddressDto = orderDto.getDeliveryAddress();
            if(deliveryAddressDto.getId() != null) {
                Optional<UserAddress> optionalUserAddress = addressRepository.findById(deliveryAddressDto.getId());
                optionalUserAddress.ifPresent(order::setDeliveryAddress);
            } else {
                UserAddress userAddress = new UserAddress();
                userAddress.setAddress(deliveryAddressDto.getAddress());
                userAddress.setCity(deliveryAddressDto.getCity());
                userAddress.setZipCode(deliveryAddressDto.getZipCode());
                userAddress.setClientUser(optionalClientUser.get());
                userAddress = addressRepository.save(userAddress);
                order.setDeliveryAddress(userAddress);
            }


            //save order
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            order.setDateTime(LocalDateTime.from(dateTimeFormatter.parse(LocalDateTime.now().format(dateTimeFormatter))));
            order.setStatus(OrderStatus.OPEN);
            order.setClientUser(optionalClientUser.get());
            order.setDeliveryUser(optionalDeliveryUser.get());
            order.setPaymentType(PaymentType.valueOf(orderDto.getPaymentType()));
            order.setDeliveryTax(orderDto.getDeliveryTax());
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
            savedOrder.setValue(value.get());

            //send notification
//            Notification notification = new Notification();
//            notification.setOrder(savedOrder);
//            notification.setType(NotificationType.ORDER_RECEIVED);
//            notification.setSeen(false);
//            notificationRepository.save(notification);
            return OrderDto.entityToDto(orderRepository.save(savedOrder));
           // return OrderDto.entityToDto(orderRepository.save(savedOrder));
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

    public OrderDto updateOrderStatus(Long orderId, String newStatus){
        Optional<Order> optionalOrder = findOrderById(orderId);
        if(optionalOrder.isPresent() && EnumUtils.isValidEnum(OrderStatus.class, newStatus)){
            Order order = optionalOrder.get();
            OrderStatus orderStatus = OrderStatus.valueOf(newStatus);
            order.setStatus(orderStatus);

            //send notification if order is picked up
            if (orderStatus.equals(OrderStatus.PICKED_UP)) {
                Notification notification = new Notification();
                notification.setSeen(false);
                notification.setOrder(order);
                notification.setType(NotificationType.ORDER_PICKED_UP);
                notificationRepository.save(notification);
            }

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
    //or create new open order
    public Optional<Order> getCurrentOpenOrder(Long clientId){
        List<Order> orders = orderRepository.findByClientUserId(clientId);
        List<Order> expiredOpenOrders = orders.stream().filter(matchOrder ->
                matchOrder.getDateTime().isBefore(LocalDateTime.now())).collect(Collectors.toList());
        for (Order o: expiredOpenOrders) {
            deleteOrder(o.getId());
        }
        Optional<Order> order = orders.stream().filter(matchOrder ->
                matchOrder.getDateTime().toString().substring(0,10).equals(LocalDateTime.now().toString().substring(0,10))).findAny();
        if(order.isPresent()){
            if(order.get().getStatus().equals(OrderStatus.OPEN)){
                return order;
            }
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
            return orderRepository.save(order);
        }
        return null;
    }

    public OrderDto addOrderProduct(Long clientId, Long productId, int quantity){
        Optional<Order> optionalOrder = getCurrentOpenOrder(clientId);
        Optional<OrderProduct> optionalOrderProduct = orderProductRepository.findById(productId);

        Order order = new Order();
        if(optionalOrder.isPresent() && optionalOrderProduct.isPresent()) {
            order = optionalOrder.get();
        }else{
            order = createOpenOrder(clientId, optionalOrderProduct.get().getProduct().getRestaurant().getId());
            OrderProduct op = new OrderProduct();
            op.setQuantity(quantity);
            orderProductRepository.save(op);
            return OrderDto.entityToDto(orderRepository.save(order));
        }
        return null;
    }

    public List<Order> getAll(){
        return orderRepository.findAll();
    }

    public double getTotalCounts(){
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
}
