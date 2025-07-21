package com.cargo.services;

import com.cargo.models.dtos.OrderDto;
import com.cargo.models.entities.Order;
import com.cargo.models.entities.OrderStatus;
import com.cargo.models.entities.User;
import com.cargo.models.mappers.OrderMapper;
import com.cargo.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional; // Optional import'u eklendi

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, ProductService productService, UserService userService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productService = productService;
        this.userService = userService;
    }

    public Page<OrderDto> getOrdersWithPagination(Pageable pageable) {
            Page<Order> orders = orderRepository.findAll(pageable);
            Page<OrderDto> orderDtos = orders.map(orderMapper::toDto);
            return orderDtos;
    }

    /**
     * Sipariş entity'sini ID ile getirir. Diğer servislerin kullanması için.
     * @param id Sipariş ID'si
     * @return Optional<Order> nesnesi
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderEntityById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Sipariş durumunu iş kurallarını atlayarak doğrudan günceller.
     * Genellikle sistemsel işlemler (sevkiyat oluşturma gibi) için kullanılır.
     * @param orderId Güncellenecek siparişin ID'si
     * @param newStatus Yeni sipariş durumu
     * @return Güncellenmiş OrderDto
     */
    @Transactional
    public OrderDto updateOrderStatusDirectly(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
        return orderMapper.toDto(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByCustomerId(Long customerId, Pageable pageable) {
        User customer = userService.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
        Page<Order> orders = orderRepository.findByCustomer(customer, pageable);
        return orders.map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        return orders.map(orderMapper::toDto);
    }

}