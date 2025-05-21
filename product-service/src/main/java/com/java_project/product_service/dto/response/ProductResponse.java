package com.java_project.product_service.dto.response;

import com.java_project.product_service.entity.Distributor;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String productName;
    BigDecimal unitPrice;
    int unitsInStock;
    int unitsInOrder;
    String author;
    String translator;
    int quantityOfPage;
    String publisher;
    int publicYear;
    String language;
    String weight;
    String size;
    String bookLayout;
    String imageFile;
    SimpleCategoryResponse category;
    Distributor distributor;
    String slug;
    String description;
}
