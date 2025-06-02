package com.java_project.comment_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cloud.openfeign.FeignClient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String avatar;
    String username;
    String firstName;
    String lastName;
}
