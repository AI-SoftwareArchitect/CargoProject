package com.cargo.models.mappers;

import com.cargo.models.dtos.ProductDto;
import com.cargo.models.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product entity);

    @Mapping(target = "id", ignore = true) // ID veritabanı tarafından oluşturulur
    Product toEntity(ProductDto dto);

    List<ProductDto> toDtoList(List<Product> entities);
}