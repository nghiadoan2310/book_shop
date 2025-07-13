package com.java_project.chat_service.mapper;

import com.java_project.chat_service.dto.response.ConversationResponse;
import com.java_project.chat_service.entity.Conversation;
import org.mapstruct.Mapper;

@Mapper
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);
}
