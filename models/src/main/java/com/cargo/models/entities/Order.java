package com.cargo.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;
import com.cargo.models.entities.OrderStatus; // Ensure this import is correct

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    // One-to-One relationship with Shipment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Shipment shipment; // This should exist and be mappedBy "order" in Shipment entity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Date createdAt;

    // Opsiyonel:
    // @Column(nullable = false)
    // private Date updatedAt;

    // Business logic metotları (opsiyonel)
    public boolean canBeApproved() {
        return this.status == OrderStatus.PENDING;
    }

    public boolean canBeCancelled() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.APPROVED;
    }
}