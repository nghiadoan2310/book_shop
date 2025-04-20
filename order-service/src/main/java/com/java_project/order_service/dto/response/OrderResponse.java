package com.java_project.order_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String fullName;
    String phoneNumber;
    String email;
    String shipAddress;
    String shipRegion;
    String shipDistrict;
    String shipCity;
    String node;
}
