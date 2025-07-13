package com.java_project.comment_service.repository;

import com.java_project.comment_service.entity.CommentLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CommentLikeRepository extends MongoRepository<CommentLike, String> {
    void deleteByUserId(String userId);
    CommentLike findByUserId(String userId);
    Set<String> findUserIdByCommentId(String commentId);
}
