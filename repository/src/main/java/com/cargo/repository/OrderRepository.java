package com.cargo.repository;

import com.cargo.models.entities.Order;
import com.cargo.models.entities.OrderStatus; // Doğru enum paketi
import com.cargo.models.entities.User; // <-- BU SATIRI EKLEYİN
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    List<Order> findByCustomerAndStatusOrderByCreatedAtDesc(User customer, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("startDate") LocalDateTime startDate);

    Page<Order> findByCustomer(User customer, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

}