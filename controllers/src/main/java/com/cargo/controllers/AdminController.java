package com.cargo.controllers;

import com.cargo.models.dtos.OrderDto;
import com.cargo.models.entities.Order;
import com.cargo.services.OrderService;
import com.cargo.services.ProductService;
import com.cargo.services.ShipmentService;
import com.cargo.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
@Tag(name = "Order Management", description = "Siparişleri yönetmek için operasyonlar")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final OrderService orderService;
    private final ProductService productService;
    private final ShipmentService shipmentService;
    private final UserService userService;

    @GetMapping("/orders")
    public Page<OrderDto> getAllOrders(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    )
    {
        log.info("Fetching all orders from admin.");
        return orderService.getOrdersWithPagination(pageable);
    }





}