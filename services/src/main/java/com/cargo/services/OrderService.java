package com.cargo.services;

import com.cargo.models.dtos.OrderDto;
import com.cargo.models.dtos.OrderItemDto;
import com.cargo.models.entities.Order;
import com.cargo.models.entities.OrderItem;
import com.cargo.models.entities.Product;
import com.cargo.models.entities.User;
import com.cargo.models.mappers.OrderMapper;
import com.cargo.models.mappers.OrderItemMapper;
import com.cargo.repository.OrderRepository;
import com.cargo.models.entities.OrderStatus;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper,
                        OrderItemMapper orderItemMapper, ProductService productService,
                        UserService userService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productService = productService;
        this.userService = userService;
    }

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);

        User customer = userService.findById(orderDto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + orderDto.getCustomerId()));
        order.setCustomer(customer);

        order.setCreatedAt(new Date());

        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        List<OrderItem> orderItems = orderDto.getOrderItems().stream()
                .map(itemDto -> {
                    Product product = productService.getProductEntityById(itemDto.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemDto.getProductId()));

                    if (product.getStock() < itemDto.getQuantity()) {
                        throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
                    }
                    productService.decreaseStock(product.getId(), itemDto.getQuantity());

                    OrderItem orderItem = orderItemMapper.toEntity(itemDto);
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setPrice(product.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }

        if (newStatus == OrderStatus.CANCELLED && (order.getStatus() == OrderStatus.APPROVED || order.getStatus() == OrderStatus.SHIPPED)) {
            order.getOrderItems().forEach(item -> productService.increaseStock(item.getProduct().getId(), item.getQuantity()));
        } else if (order.getStatus() == OrderStatus.PENDING && newStatus == OrderStatus.CANCELLED) {
            order.getOrderItems().forEach(item -> productService.increaseStock(item.getProduct().getId(), item.getQuantity()));
        }

        if (newStatus == OrderStatus.APPROVED && order.getStatus() == OrderStatus.PENDING) {
            // Stoklar zaten oluşturma anında düşürüldüğü için burada bir şey yapmaya gerek yok.
            // Bu kısım daha çok, onaylandığında başka bir iş akışını (örn. sevkiyatı başlatma) tetiklemek içindir.
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public OrderDto updateOrderStatusDirectly(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderEntityById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orderMapper.toDtoList(orders);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        User customer = userService.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
        List<Order> orders = orderRepository.findByCustomerOrderByCreatedAtDesc(customer);
        return orderMapper.toDtoList(orders);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        return orderMapper.toDtoList(orders);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == OrderStatus.APPROVED || newStatus == OrderStatus.CANCELLED;
            case APPROVED:
                return newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED:
                return newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED;
            case DELIVERED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }
}