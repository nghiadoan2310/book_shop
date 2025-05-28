package com.java_project.cart_service.mapper;

import com.java_project.cart_service.dto.response.AddToCartResponse;
import com.java_project.cart_service.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "productId", ignore = true)
    AddToCartResponse toAddToCartResponse(ProductResponse productResponse);
}
