package com.java_project.comment_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String productId;
    ProfileResponse userProfile;
    int rating;
    long like;
    boolean liked;
    String comment;
    String createAt;
    String updateAt;
}
