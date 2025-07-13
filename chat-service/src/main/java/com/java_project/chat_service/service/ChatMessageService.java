package com.java_project.chat_service.service;

import com.java_project.chat_service.dto.request.ChatMessageRequest;
import com.java_project.chat_service.dto.response.ChatMessageResponse;
import com.java_project.chat_service.entity.ChatMessage;
import com.java_project.chat_service.exception.AppException;
import com.java_project.chat_service.exception.ErrorCode;
import com.java_project.chat_service.mapper.ChatMessageMapper;
import com.java_project.chat_service.repository.ChatMessageRepository;
import com.java_project.chat_service.repository.ConversationRepository;
import com.java_project.chat_service.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {
    ChatMessageRepository chatMessageRepository;
    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ChatMessageMapper chatMessageMapper;

    public ChatMessageResponse createMessage(ChatMessageRequest request) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        conversationRepository.findById(request.getConversationId())
                //Nếu như không tìm thấy cuộc trò chuyện -> bắn ra lỗi
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
                //Tìm trong danh sách thành viên có người dùng đang chat không
                .filter(participant -> participant.getUserId().equals(currentUserId))
                //Nếu không có bắn ra lỗi
                .findAny().orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND_IN_CONVERSATION));

        var chatMessage = ChatMessage.builder()
                .message(request.getMessage())
                .conversationId(request.getConversationId())
                .sender(profileClient.getMyProfile().getResult())
                .createDate(Instant.now())
                .build();

        return toChatMessageResponse(chatMessageRepository.save(chatMessage));
    }

    public List<ChatMessageResponse> getChatMessages(String conversationId, int page, int size) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        conversationRepository.findById(conversationId)
                //Nếu như không tìm thấy cuộc trò chuyện -> bắn ra lỗi
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
                //Tìm trong danh sách thành viên có người dùng đang chat không
                .filter(participant -> participant.getUserId().equals(currentUserId))
                //Nếu không có bắn ra lỗi
                .findAny().orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND_IN_CONVERSATION));

        Sort sort = Sort.by("createDate").descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        var chatMessages = chatMessageRepository.findAllByConversationId(conversationId, pageable);

        return chatMessages.stream().map(this::toChatMessageResponse).toList();
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        var chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);

        chatMessageResponse.setMe(chatMessage.getSender().getUserId().equals(currentUserId));

        return chatMessageResponse;
    }
}
