package com.java_project.product_service.entity;

import com.java_project.product_service.dto.response.SimpleCategoryResponse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String productName;

    @Column(precision = 10, scale = 2) //Tối đa 10 số, làm tròn 2 số sau dấu (,)
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
    String slug;

    @ManyToOne
    Category category;

    @ManyToOne
    Distributor distributor;

    @Lob //đánh dấu trường chứa nội dung dài
    String description;
}
