package com.java_project.order_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String fullName;
    String phoneNumber;
    String email;
    String shipAddress;
    String shipRegion;
    String shipDistrict;
    String shipCity;
    String node;
}
