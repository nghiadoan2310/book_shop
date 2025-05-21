package com.java_project.product_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DistributorResponse {
    String id;
    String name;
    String description;
    String imageFile;
    String slug;
}
