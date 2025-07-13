package com.java_project.chat_service.entity;

import com.java_project.chat_service.dto.response.ProfileResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@Document(collection = "conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @MongoId
    String id;

    String type;

    @Indexed(unique = true)
    String participantsHash;

    List<ProfileResponse> participants;

    Instant createDate;
    Instant modifiedDate;
}
