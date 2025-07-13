package com.java_project.chat_service.controller;

import com.java_project.chat_service.dto.ApiResponse;
import com.java_project.chat_service.dto.request.ChatMessageRequest;
import com.java_project.chat_service.dto.response.ChatMessageResponse;
import com.java_project.chat_service.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageController {
    ChatMessageService chatMessageService;

    @PostMapping("/create")
    ApiResponse<ChatMessageResponse> createMessage(@RequestBody @Valid ChatMessageRequest request) {
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatMessageService.createMessage(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ChatMessageResponse>> getChatMessages(@RequestParam("conversationId") String conversationId,
                                                          @RequestParam("page") int page,
                                                          @RequestParam("size") int size) {
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .result(chatMessageService.getChatMessages(conversationId, page, size))
                .build();
    }
}
