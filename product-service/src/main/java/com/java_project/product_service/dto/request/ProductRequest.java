package com.java_project.product_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String productName;
    BigDecimal unitPrice;
    int unitsInStock;
    String author;
    String translator;
    int quantityOfPage;
    String publisher;
    int publicYear;
    String language;
    String weight;
    String size;
    String bookLayout;
    MultipartFile imageFile;
    String category;
    String distributor;
    String description;
}
