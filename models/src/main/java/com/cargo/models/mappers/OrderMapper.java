package com.cargo.models.mappers;

import com.cargo.models.dtos.OrderDto;
import com.cargo.models.dtos.OrderItemDto; // Make sure this is imported
import com.cargo.models.entities.OrderStatus; // Make sure this is imported
import com.cargo.models.entities.Order;
import com.cargo.models.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "orderItems", target = "orderItems") // Ensure this mapping is correct
    @Mapping(source = "shipment.id", target = "shipmentId")
    @Mapping(source = "shipment.trackingNumber", target = "shipmentTrackingNumber")
    @Mapping(source = "shipment.status", target = "shipmentStatus")
    // CORRECTED: Use 'shipper' instead of 'shippedBy'
    @Mapping(source = "shipment.shipper.id", target = "shipmentShipperId")
    @Mapping(source = "shipment.shipper.name", target = "shipmentShipperName")
    @Mapping(source = "shipment.shippingAddress", target = "shipmentShippingAddress")
    @Mapping(source = "shipment.shipmentDate", target = "shipmentDate")
    @Mapping(source = "shipment.estimatedDeliveryDate", target = "estimatedDeliveryDate")
    @Mapping(source = "shipment.actualDeliveryDate", target = "actualDeliveryDate")
    OrderDto toDto(Order order);

    // For toEntity, ensure you're not trying to map complex objects directly
    // and let the service handle linking entities by ID.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true) // Set in service by customerId
    @Mapping(target = "orderItems", ignore = true) // Set in service via DTO items
    @Mapping(target = "shipment", ignore = true) // Set in service via shipmentId or on shipment creation
    Order toEntity(OrderDto dto);

    List<OrderDto> toDtoList(List<Order> orders);
}