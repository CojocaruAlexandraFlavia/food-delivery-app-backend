package com.example.fooddelivery.service;

import com.example.fooddelivery.model.*;
import com.example.fooddelivery.model.dto.ProductDto;
import com.example.fooddelivery.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    private final CategoryService categoryService;
    private final RestaurantService restaurantService;
    //private final ClientUserService clientUserService;

    @Autowired
    public ProductService(ProductRepository productRepository, OrderProductRepository orderProductRepository,
                          CategoryService categoryService,
                          RestaurantService restaurantService){
                          //ClientUserService clientUserService) {
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
        //this.clientUserService = clientUserService;
    }
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<OrderProduct> findOrderProductById(Long id) {
        return orderProductRepository.findById(id);
    }

    public ProductDto saveProduct(@NotNull ProductDto productDto){
        Product product = new Product();
        Optional<Category> optionalCategory = categoryService.findCategoryById(productDto.getCategoryId());
        Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(productDto.getRestaurantId());
        //Optional<ClientUser> optionalClientUser = clientUserService.findClientUserById(productDto.getClientUserId());

        if(optionalCategory.isPresent() && optionalRestaurant.isPresent() ){
                //&& optionalClientUser.isPresent()){
            //save product
            product.setPrice(productDto.getPrice());
            product.setName(productDto.getName());
            product.setDiscount(productDto.getDiscount());
            product.setIngredients(productDto.getIngredients());

            product.setCategory(optionalCategory.get());
            product.setRestaurant(optionalRestaurant.get());
            product.setAvailability(true);
            product = productRepository.save(product);
            return ProductDto.entityToDto(product);
        }
        return null;
    }

    public ProductDto updateProduct(Long productId, ProductDto productDto){
        Optional<Product> optionalProduct = findProductById(productId);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setPrice(productDto.getPrice());
            product.setName(productDto.getName());
            product.setDiscount(productDto.getDiscount());
            product.setIngredients(productDto.getIngredients());

            return ProductDto.entityToDto(productRepository.save(product));
        }
        return null;
    }

    public ProductDto changeProductAvailability(Long productId){
        if(productId != null){
            Optional<Product> optionalProduct = findProductById(productId);
            if(optionalProduct.isPresent()){
                Product product = optionalProduct.get();
                product.setAvailability(!product.isAvailability());
                product = productRepository.save(product);
                return ProductDto.entityToDto(product);
            }
        }
        return null;
    }

    public ProductDto addOrderProduct(Long productId, Long orderProductId, int quantity){
        Optional<Product> optionalProduct = findProductById(productId);
        Optional<OrderProduct> optionalOrderProduct = findOrderProductById(orderProductId);

        if(optionalProduct.isPresent() && optionalOrderProduct.isPresent()){
            Product product = optionalProduct.get();
            OrderProduct op = new OrderProduct();
            op.setQuantity(quantity);
            orderProductRepository.save(op);
            return ProductDto.entityToDto(productRepository.save(product));
        }
        return null;
    }
}
