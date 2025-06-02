package com.java_project.comment_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentConsumerRequest {
    String productId;
    String userId;

    @Min(value = 1)
    @Max(value = 5)
    int rating;

    String comment;
}
