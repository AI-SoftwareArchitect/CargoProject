package com.cargo.models.dtos;

import com.cargo.models.entities.ShipmentStatus; // Make sure this import is correct
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDto {
    private Long id;
    private Long orderId; // To link to an Order
    private String trackingNumber;
    private ShipmentStatus status;
    private String shippingAddress;
    private Long shipperId; // ID of the user (e.g., employee) who initiated the shipment
    private Date shipmentDate;
    private Date estimatedDeliveryDate;
    private Date actualDeliveryDate;
    private String notes;
}