package com.java_project.cart_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddToCartResponse {
    String productId;
    String productName;
    BigDecimal unitPrice;
    int quantity;
    String imageFile;
    String slug;
}
