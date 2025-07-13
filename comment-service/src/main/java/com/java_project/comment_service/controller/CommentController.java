package com.java_project.comment_service.controller;

import com.java_project.comment_service.dto.ApiResponse;
import com.java_project.comment_service.dto.PageResponse;
import com.java_project.comment_service.dto.request.CommentRequest;
import com.java_project.comment_service.dto.response.CommentLikeResponse;
import com.java_project.comment_service.dto.response.CommentResponse;
import com.java_project.comment_service.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping("/create")
    ApiResponse<String> createComment(@RequestBody CommentRequest request) {
        return ApiResponse.<String>builder()
                .result(commentService.createComment(request))
                .build();
    }

    @GetMapping("/{productId}")
    ApiResponse<PageResponse<CommentResponse>> getCommentsOfProduct(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @PathVariable("productId") String productId) {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getCommentsOfProduct(page, size, productId))
                .build();
    }

    @PostMapping("/{commentId}/like")
    ApiResponse<CommentLikeResponse> CommentLike(@PathVariable("commentId") String commentId) {
        return ApiResponse.<CommentLikeResponse>builder()
                .result(commentService.CommentLike(commentId))
                .build();
    }
}
