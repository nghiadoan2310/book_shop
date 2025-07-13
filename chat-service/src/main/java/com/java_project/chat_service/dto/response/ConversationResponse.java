package com.java_project.chat_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String id;
    String type;
    String participantsHash;
    String conversationAvatar;
    String conversationName;
    List<ProfileResponse> participants;
    Instant createDate;
    Instant modifiedDate;
}
