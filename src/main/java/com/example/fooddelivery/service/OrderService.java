package com.example.fooddelivery.service;

import com.example.fooddelivery.enums.NotificationType;
import com.example.fooddelivery.enums.OrderStatus;
import com.example.fooddelivery.enums.PaymentType;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.*;
import com.example.fooddelivery.model.dto.requests.AddOrderProductRequest;
import com.example.fooddelivery.model.dto.requests.AddUserAddressRequest;
import com.example.fooddelivery.model.dto.requests.SendOrder;
import com.example.fooddelivery.repository.NotificationRepository;
import com.example.fooddelivery.repository.OrderProductRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.repository.UserAddressRepository;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final UserAddressRepository userAddressRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, NotificationRepository notificationRepository,
                        OrderProductRepository productRepository, ProductService productService,
                        ClientUserService clientUserService, DeliveryUserService deliveryUserService,
                        UserAddressRepository addressRepository, RestaurantService restaurantService, UserAddressRepository userAddressRepository) {
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
        this.orderProductRepository = productRepository;
        this.productService = productService;
        this.clientUserService = clientUserService;
        this.deliveryUserService = deliveryUserService;
        this.addressRepository = addressRepository;
        this.restaurantService = restaurantService;
        this.userAddressRepository = userAddressRepository;
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
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
                viewCartDto.setNumber(viewOrder.getOrderNumber());
                List<OrderProductDto> orderProducts = viewOrder.getProducts().stream().map(OrderProductDto::entityToDto).collect(toList());
                viewCartDto.setProducts(orderProducts);
                viewCartDto.setValue(order.get().getValue());
                viewCartDto.setDeliveryTax(order.get().getDeliveryTax());
                if(order.get().getPaymentType().equals(PaymentType.CASH_ON_DELIVERY)){
                    viewCartDto.setPaymentType("Cash on delivery");
                }else{
                    viewCartDto.setPaymentType("Online payment");
                }

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
            OrderProduct removeOrderProduct = new OrderProduct();
            boolean isFound = false;
            for (OrderProduct orderProduct1: orderProducts) {
                if (orderProduct1.getProduct().getId().equals(productId)) {
                    if (orderProduct1.getQuantity() - 1 == 0) {
                        removeOrderProduct = orderProduct1;
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
                updatedOrder.getProducts().remove(removeOrderProduct);
                updatedOrder = orderRepository.save(updatedOrder);
                return viewCart(updatedOrder.getClientUser().getId());
            }

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
            boolean isFound = false;
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
                return viewCart(updatedOrder.getClientUser().getId());
            }
        }
        return null;
    }

    public ViewCartDto deleteProduct(Long productId, Long clientId){
        Optional<Product> orderProduct = productService.findProductById(productId);
        Optional<Order> order = getCurrentOpenOrder(clientId);

        OrderProduct removeOrderProduct = new OrderProduct();
        if(orderProduct.isPresent() && order.isPresent()){
            Product product = orderProduct.get();
            Order updatedOrder = order.get();
            List<OrderProduct> orderProducts = updatedOrder.getProducts();
            boolean isFound = false;
            int quantity = 0;
            for (OrderProduct orderProduct1: orderProducts) {
                if (orderProduct1.getProduct().getId().equals(productId)) {
                    removeOrderProduct = orderProduct1;
                    orderProductRepository.deleteByOrderIdAndProductId(updatedOrder.getId(), product.getId());
                    quantity = orderProduct1.getQuantity();
                    isFound = true;
                }
            }

            if(isFound){
                double productPriceWithDiscountApplied = product.getPrice() -
                        product.getDiscount() / 100 * product.getPrice();
                updatedOrder.getProducts().remove(removeOrderProduct);
                updatedOrder.setValue(updatedOrder.getValue() - (productPriceWithDiscountApplied * quantity));
                updatedOrder = orderRepository.save(updatedOrder);
                return viewCart(updatedOrder.getClientUser().getId());
            }

        }

        return null;
    }

    public OrderDto updateOrderAddress(AddUserAddressRequest addUserAddressRequest){
        Long clientId = addUserAddressRequest.getClientId();
        String city = addUserAddressRequest.getCity();
        String zipCode = addUserAddressRequest.getZipCode();
        String address = addUserAddressRequest.getAddress();
        Optional<Order> order = getCurrentOpenOrder(clientId);
        if(order.isPresent()) {
            Optional<ClientUser> clientUser = clientUserService.findClientUserById(clientId);
            if(clientUser.isPresent()){
                Order currentOrder = order.get();
                UserAddress userAddress = new UserAddress();
                userAddress.setAddress(address);
                userAddress.setCity(StringUtils.capitalize(city.toLowerCase()));
                userAddress.setZipCode(zipCode);
                userAddress.setClientUser(clientUser.get());

                userAddressRepository.save(userAddress);
                currentOrder.setDeliveryAddress(userAddress);
                return OrderDto.entityToDto(orderRepository.save(currentOrder));
            }
        }
        return null;
    }

    public OrderDto sendOrder(SendOrder sendOrder){
        Long clientId = sendOrder.getClientId();
        Long addressId = sendOrder.getAddressId();
        Optional<Order> order = getCurrentOpenOrder(clientId);
        if(order.isPresent()){
            Order currentOrder = order.get();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            currentOrder.setDateTime(LocalDateTime.from(dateTimeFormatter.parse(LocalDateTime.now().format(dateTimeFormatter))));
            currentOrder.setTotalPrice(currentOrder.getValue() + currentOrder.getDeliveryTax());
            Optional<UserAddress> address = addressRepository.findById(addressId);
            if (address.isPresent()) {
                UserAddress userAddress = address.get();
                currentOrder.setDeliveryAddress(userAddress);
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
            orderProductRepository.deleteAll(o.getProducts());
            deleteOrder(o.getId());
        }

        return orders.stream().filter(matchOrder ->
                matchOrder.getDateTime().toString().substring(0,10).equals(LocalDateTime.now().toString().substring(0,10))).filter(matchOrder ->
                matchOrder.getStatus().equals(OrderStatus.OPEN)).findAny();
    }

    public Order createOpenOrder(Long clientId, Long restaurantId){
        Optional<ClientUser> clientUser = clientUserService.findClientUserById(clientId);
        Optional<Restaurant> restaurant = restaurantService.findRestaurantById(restaurantId);
        if(clientUser.isPresent() && restaurant.isPresent()){
            Order order = new Order();

            //set order number
            Order lastSavedOrder = orderRepository.findFirstByOrderByIdDesc();
            if(lastSavedOrder != null) {
                order.setOrderNumber(lastSavedOrder.getOrderNumber() + 1);
            } else {
                order.setOrderNumber(1);
            }
            //save order
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            order.setDateTime(LocalDateTime.from(dateTimeFormatter.parse(LocalDateTime.now().format(dateTimeFormatter))));
            order.setStatus(OrderStatus.OPEN);
            order.setClientUser(clientUser.get());

            order.setDeliveryTax(restaurant.get().getDeliveryTax());
            order.setPaymentType(PaymentType.CASH_ON_DELIVERY);
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
        Order order;

        if(productOptional.isPresent()){
            Product product = productOptional.get();
            Long restaurantId = productOptional.get().getRestaurant().getId();
            if(optionalOrder.isPresent()){
                order = optionalOrder.get();
                boolean found = false;
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
            } else {
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
                        product.getDiscount() * product.getPrice();
            Double updatedValue = oldValue + (productPriceWithDiscountApplied * quantity);
            order.setValue(updatedValue);
            order = orderRepository.save(order);
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
        List<Order> allOrders = getAll();

        double price = getTotalCounts();
        CheckOrderCountDto checkOrderCountDto =  new CheckOrderCountDto();
        checkOrderCountDto.setTotalCount(price);
        checkOrderCountDto.setNumberOfOrders((long) allOrders.size());

        List<List<Product>> allOrderedProductsLists = allOrders.stream().map(order -> order.getProducts().stream()
                .map(OrderProduct::getProduct).collect(toList())).collect(toList());
        List<Product> products = allOrderedProductsLists.stream().flatMap(List::stream).collect(toList());
        List<Product> distinctProducts = products.stream().distinct().collect(toList());
        checkOrderCountDto.setNumberOfProducts((long) distinctProducts.size());

        List<String> distinctOrdersCity = allOrders.stream().map(order -> order.getDeliveryAddress().getCity())
                .distinct().collect(toList());
        checkOrderCountDto.setNumberOfCities((long) distinctOrdersCity.size());

        List<Restaurant> distinctOrdersRestaurants = allOrders.stream().map(order -> order.getProducts()
                .get(0).getProduct().getRestaurant()).distinct().collect(toList());
        checkOrderCountDto.setNumberOfRestaurants((long) distinctOrdersRestaurants.size());
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
