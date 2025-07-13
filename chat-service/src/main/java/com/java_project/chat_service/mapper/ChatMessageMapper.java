package com.java_project.chat_service.mapper;

import com.java_project.chat_service.dto.request.ChatMessageRequest;
import com.java_project.chat_service.dto.response.ChatMessageResponse;
import com.java_project.chat_service.entity.ChatMessage;
import org.mapstruct.Mapper;

@Mapper
public interface ChatMessageMapper {
    ChatMessage toChatMessage(ChatMessageRequest request);
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);
}
