package com.java_project.comment_service.entity;

import com.java_project.comment_service.dto.response.ProfileResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Document(collection = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @MongoId
    String id;

    String productId;
    ProfileResponse userProfile;
    int rating;
    long like;
    String comment;
    Instant createAt;
    Instant updateAt;
}
