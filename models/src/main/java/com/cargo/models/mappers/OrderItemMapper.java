package com.cargo.models.mappers;

import com.cargo.models.dtos.OrderItemDto;
import com.cargo.models.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "price", target = "price") // OrderItem entity'sindeki price'ı map'le
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true) // İlişki servis katmanında yönetilir
    @Mapping(target = "product", ignore = true) // İlişki servis katmanında yönetilir
    OrderItem toEntity(OrderItemDto dto);

    List<OrderItemDto> toDtoList(List<OrderItem> orderItems);
}