package com.java_project.post_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    String id;
    String content;
    String userId;
    String username;
    String created; //Thời gian kể từ ngày đăng bài (VD: 2 giây trước, 2 giờ trước,...)
    Instant createdDate; //Thời điểm tạo bài post
    Instant modifiedDate; //Thời gian chỉnh sửa gần nhất
}
