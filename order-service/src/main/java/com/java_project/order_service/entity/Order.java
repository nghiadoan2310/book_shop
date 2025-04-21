package com.java_project.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity //Tạo table trong DB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String fullName;
    String phoneNumber;
    String email;
    String orderDate;
    String requiredDate;
    String shippedDate;
    String shipFee;
    String shipAddress;
    String shipRegion;
    String shipDistrict;
    String shipCity;
    String node;

    @OneToMany
    Set<OrderDetail> orderDetails;
}
