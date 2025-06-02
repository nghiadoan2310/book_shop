package com.java_project.comment_service.mapper;

import com.java_project.comment_service.dto.request.CommentConsumerRequest;
import com.java_project.comment_service.dto.request.CommentRequest;
import com.java_project.comment_service.dto.response.CommentResponse;
import com.java_project.comment_service.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "like", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    Comment toComment(CommentRequest request);

    CommentResponse toCommentResponse(Comment comment);

    CommentConsumerRequest toCommentConsumerRequest(CommentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "like", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    Comment toCommentFromCommentConsumerRequest(CommentConsumerRequest request);
}
