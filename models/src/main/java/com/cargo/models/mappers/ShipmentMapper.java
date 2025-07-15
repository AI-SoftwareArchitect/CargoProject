package com.cargo.models.mappers;

import com.cargo.models.dtos.ShipmentDto;
import com.cargo.models.entities.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipmentMapper {

    @Mapping(source = "order.id", target = "orderId") // Map order entity's ID to DTO's orderId
    @Mapping(source = "shipper.id", target = "shipperId") // Map shipper entity's ID to DTO's shipperId
    ShipmentDto toDto(Shipment entity);

    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "order", ignore = true) // Will be set in service
    @Mapping(target = "shipper", ignore = true) // Will be set in service
    Shipment toEntity(ShipmentDto dto);

    List<ShipmentDto> toDtoList(List<Shipment> entities);
}