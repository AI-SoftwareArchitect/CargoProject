package com.cargo.models.entities;

import com.cargo.models.entities.ShipmentStatus; // Make sure this import is correct
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-One relationship with Order
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default // Lombok builder default value
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(nullable = false)
    private String shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id") // ID of the user (e.g., employee) who initiated the shipment
    private User shipper;

    @Column(nullable = false)
    private Date shipmentDate;

    @Column
    private Date estimatedDeliveryDate;

    @Column
    private Date actualDeliveryDate;

    // Additional fields for tracking details or notes
    @Column(length = 1024)
    private String notes;
}