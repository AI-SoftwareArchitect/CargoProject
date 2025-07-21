package com.cargo.controllers;

import com.cargo.models.dtos.ProductDto;
import com.cargo.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Management", description = "Ürünleri yönetmek için operasyonlar")
public class ProductsController {

    private static final Logger log = LoggerFactory.getLogger(ProductsController.class);
    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        log.info("Fetching all active products in stock.");
        return productService.getActiveProductsInStock();
    }


}