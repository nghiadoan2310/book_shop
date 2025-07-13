package com.java_project.chat_service.service;

import com.java_project.chat_service.dto.request.ConversationRequest;
import com.java_project.chat_service.dto.response.ConversationResponse;
import com.java_project.chat_service.dto.response.ProfileResponse;
import com.java_project.chat_service.entity.Conversation;
import com.java_project.chat_service.mapper.ConversationMapper;
import com.java_project.chat_service.repository.ConversationRepository;
import com.java_project.chat_service.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ConversationMapper conversationMapper;

    public ConversationResponse createConversation(ConversationRequest request) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<String> participantIds = request.getParticipantIds();
        participantIds.add(currentUserId);

        System.out.println(participantIds);

        List<ProfileResponse> participants = profileClient.getProfiles(participantIds).getResult();

        String participantsHash = null;
        if(!ObjectUtils.isEmpty(participants) && participants.size() == 2) {
            participantsHash = toParticipantsHash(participantIds.stream().sorted().toList());
            var conversation = conversationRepository.findByParticipantsHash(participantsHash);

            if(!ObjectUtils.isEmpty(conversation)) {
                return toConversationResponse(conversation);
            }
        }

        Conversation conversation = Conversation.builder()
                .type(request.getType())
                .participantsHash(participantsHash)
                .participants(participants)
                .createDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        conversationRepository.save(conversation);

        if(!ObjectUtils.isEmpty(participants) && participants.size() == 2) {
            return toConversationResponse(conversation);
        }

        return conversationMapper.toConversationResponse(conversation);
    }

    public List<ConversationResponse> myConversations() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var conversations = conversationRepository.findAllByParticipantIdsContains(userId);

        return conversations.stream().map(conversationMapper::toConversationResponse).toList();
    }

    private String toParticipantsHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);

        return stringJoiner.toString();
    }

    //Modify dữ liệu trả về nếu type là direct
    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        conversation.getParticipants().stream()
                .filter(participant -> !participant.getUserId().equals(currentUserId))
                .findFirst().ifPresent(participant -> {
                    conversationResponse.setConversationName(participant.getUsername());
                    conversationResponse.setConversationAvatar(participant.getAvatar());
                });

        return conversationResponse;
    }
}
