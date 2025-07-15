package com.cargo.models.dtos;

import com.cargo.models.entities.OrderStatus; // Make sure this is imported
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long customerId;
    private String customerName; // Added to display customer name in DTO
    private List<OrderItemDto> orderItems;
    private OrderStatus status;
    private Date createdAt;

    // Shipment details (ensure these fields exist in your OrderDto)
    private Long shipmentId;
    private String shipmentTrackingNumber;
    private String shipmentStatus; // Assuming ShipmentStatus enum toString() can be mapped to String
    private Long shipmentShipperId; // CORRECTED: shipperId instead of shippedById
    private String shipmentShipperName; // CORRECTED: shipperName instead of shippedByName
    private String shipmentShippingAddress;
    private Date shipmentDate;
    private Date estimatedDeliveryDate;
    private Date actualDeliveryDate;
}