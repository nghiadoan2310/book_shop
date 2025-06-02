package com.java_project.comment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_project.comment_service.dto.PageResponse;
import com.java_project.comment_service.dto.request.CommentConsumerRequest;
import com.java_project.comment_service.dto.request.CommentRequest;
import com.java_project.comment_service.dto.response.CommentResponse;
import com.java_project.comment_service.mapper.CommentMapper;
import com.java_project.comment_service.repository.CommentRepository;
import com.java_project.comment_service.repository.httpClient.ProfileClient;
import com.java_project.comment_service.utils.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    ProfileClient profileClient;

    RabbitTemplate rabbitTemplate;

    CommentMapper commentMapper;
    DateTimeFormatter dateTimeFormatter;

    @Value("${rabbitmq.exchange}")
    @NonFinal
    String exchange;

    @Value("${rabbitmq.routing_key}")
    @NonFinal
    String routing_key;

    public String createComment(CommentRequest request) {
        var commentConsumerRequest = commentMapper.toCommentConsumerRequest(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        commentConsumerRequest.setUserId(authentication.getName());

        rabbitTemplate.convertAndSend(exchange, routing_key, commentConsumerRequest);

        return "Send message";
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void commentConsumer(CommentConsumerRequest request) {
        var comment = commentMapper.toCommentFromCommentConsumerRequest(request);

        comment.setUserProfile(profileClient.getProfile(request.getUserId()).getResult());
        comment.setLike(0);
        comment.setCreateAt(Instant.now());
        comment.setUpdateAt(Instant.now());

        commentRepository.save(comment);
    }

    public PageResponse<CommentResponse> getCommentsOfProduct(int page, int size, String productId) {
        Sort sort = Sort.by("createAt").descending();
        Pageable pageable = PageRequest.of(Math.max(0, page-1), size, sort);
        var commentData = commentRepository.findAllByProductId(productId, pageable);

        var commentList = commentData.getContent().stream().map(comment -> {
            var commentResponse = commentMapper.toCommentResponse(comment);
            commentResponse.setCreateAt(dateTimeFormatter.format(comment.getCreateAt()));

            return commentResponse;
        }).toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .totalPages(commentData.getTotalPages())
                .pageSize(size)
                .totalElements(commentData.getTotalElements())
                .data(commentList)
                .build();
    }
}
