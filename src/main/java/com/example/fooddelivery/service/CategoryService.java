package com.example.fooddelivery.service;

import com.example.fooddelivery.model.Category;
import com.example.fooddelivery.model.Restaurant;
import com.example.fooddelivery.repository.CategoryRepository;
import com.example.fooddelivery.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

}
