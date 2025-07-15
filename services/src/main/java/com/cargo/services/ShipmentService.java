package com.cargo.services;

import com.cargo.models.dtos.ShipmentDto;
import com.cargo.models.entities.Order;
import com.cargo.models.entities.Shipment;
import com.cargo.models.entities.User;
import com.cargo.models.mappers.ShipmentMapper;
import com.cargo.repository.ShipmentRepository;
import com.cargo.models.entities.ShipmentStatus; // Make sure this import is correct
import com.cargo.models.entities.OrderStatus; // Make sure this import is correct

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // For generating tracking numbers

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final OrderService orderService; // To interact with orders
    private final UserService userService; // To get shipper details

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository,
                           ShipmentMapper shipmentMapper,
                           OrderService orderService,
                           UserService userService) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
        this.orderService = orderService;
        this.userService = userService;
    }

    @Transactional
    public ShipmentDto createShipment(ShipmentDto shipmentDto) {
        // Validate and retrieve associated Order
        Order order = orderService.getOrderEntityById(shipmentDto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + shipmentDto.getOrderId()));

        // Ensure an order can only have one shipment
        if (order.getShipment() != null) {
            throw new IllegalArgumentException("Order with ID " + shipmentDto.getOrderId() + " already has a shipment.");
        }

        // Validate and retrieve Shipper (User who creates the shipment)
        User shipper = null;
        if (shipmentDto.getShipperId() != null) {
            shipper = userService.findById(shipmentDto.getShipperId())
                    .orElseThrow(() -> new EntityNotFoundException("Shipper user not found with ID: " + shipmentDto.getShipperId()));
        }

        Shipment shipment = shipmentMapper.toEntity(shipmentDto);
        shipment.setOrder(order); // Link shipment to order
        shipment.setShipper(shipper); // Link shipment to shipper

        // Generate a unique tracking number
        shipment.setTrackingNumber(generateUniqueTrackingNumber());
        shipment.setShipmentDate(new Date()); // Set shipment creation date

        // Set initial status if not provided (should be PENDING by default in entity, but good to ensure)
        if (shipment.getStatus() == null) {
            shipment.setStatus(ShipmentStatus.PENDING);
        }

        // Update Order status to SHIPPED or PROCESSING as shipment is created
        orderService.updateOrderStatusDirectly(order.getId(), OrderStatus.SHIPPED); // Assuming order moves to SHIPPED when shipment is created

        Shipment savedShipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDto(savedShipment);
    }

    @Transactional
    public ShipmentDto updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus, Date actualDeliveryDate, String notes) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with ID: " + shipmentId));

        // Validate status transition (example logic)
        if (!isValidStatusTransition(shipment.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid shipment status transition from " + shipment.getStatus() + " to " + newStatus);
        }

        shipment.setStatus(newStatus);
        if (newStatus == ShipmentStatus.DELIVERED && actualDeliveryDate != null) {
            shipment.setActualDeliveryDate(actualDeliveryDate);
            // Optionally update related order status to DELIVERED
            orderService.updateOrderStatusDirectly(shipment.getOrder().getId(), OrderStatus.DELIVERED);
        }
        if (newStatus == ShipmentStatus.CANCELLED || newStatus == ShipmentStatus.RETURNED) {
            // Optionally update related order status to CANCELLED or RETURNED
            orderService.updateOrderStatusDirectly(shipment.getOrder().getId(), OrderStatus.CANCELLED); // Or a specific RETURNED OrderStatus
        }

        if (notes != null && !notes.isEmpty()) {
            shipment.setNotes(notes);
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDto(updatedShipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentDto> getAllShipments() {
        return shipmentMapper.toDtoList(shipmentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ShipmentDto getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with ID: " + id));
        return shipmentMapper.toDto(shipment);
    }

    @Transactional(readOnly = true)
    public Optional<ShipmentDto> getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .map(shipmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ShipmentDto> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(shipmentMapper::toDto);
    }

    // Helper method to generate unique tracking number
    private String generateUniqueTrackingNumber() {
        String trackingNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(); // Example: 16-char alphanumeric
        // In a real application, you might want to check for uniqueness in the DB
        return trackingNumber;
    }

    // Example status transition logic
    private boolean isValidStatusTransition(ShipmentStatus currentStatus, ShipmentStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == ShipmentStatus.PROCESSING || newStatus == ShipmentStatus.CANCELLED;
            case PROCESSING:
                return newStatus == ShipmentStatus.SHIPPED || newStatus == ShipmentStatus.CANCELLED || newStatus == ShipmentStatus.DELAYED;
            case SHIPPED:
                return newStatus == ShipmentStatus.IN_TRANSIT || newStatus == ShipmentStatus.DELAYED || newStatus == ShipmentStatus.CANCELLED;
            case IN_TRANSIT:
                return newStatus == ShipmentStatus.DELIVERED || newStatus == ShipmentStatus.DELAYED || newStatus == ShipmentStatus.RETURNED;
            case DELAYED:
                return newStatus == ShipmentStatus.PROCESSING || newStatus == ShipmentStatus.SHIPPED || newStatus == ShipmentStatus.IN_TRANSIT || newStatus == ShipmentStatus.CANCELLED || newStatus == ShipmentStatus.RETURNED;
            case DELIVERED:
            case CANCELLED:
            case RETURNED:
                return false; // Terminal states
            default:
                return false;
        }
    }
}