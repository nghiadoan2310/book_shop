package com.java_project.comment_service.service;

import com.java_project.comment_service.dto.PageResponse;
import com.java_project.comment_service.dto.request.CommentConsumerRequest;
import com.java_project.comment_service.dto.request.CommentRequest;
import com.java_project.comment_service.dto.response.CommentLikeResponse;
import com.java_project.comment_service.dto.response.CommentResponse;
import com.java_project.comment_service.entity.CommentLike;
import com.java_project.comment_service.exception.AppException;
import com.java_project.comment_service.exception.ErrorCode;
import com.java_project.comment_service.mapper.CommentMapper;
import com.java_project.comment_service.repository.CommentLikeRepository;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    CommentLikeRepository commentLikeRepository;
    ProfileClient profileClient;

    RabbitTemplate rabbitTemplate;
    RedisTemplate<String, String> redisTemplate;

    CommentMapper commentMapper;
    DateTimeFormatter dateTimeFormatter;

    @Value("${rabbitmq.exchange}")
    @NonFinal
    String exchange;

    @Value("${rabbitmq.routing_key}")
    @NonFinal
    String routing_key;

    static String COMMENT_LIKE_KEY_PREFIX = "comment:likes:";

    public String createComment(CommentRequest request) {
        var commentConsumerRequest = commentMapper.toCommentConsumerRequest(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        commentConsumerRequest.setUserId(authentication.getName());

        //Gửi vào hàng đợi
        rabbitTemplate.convertAndSend(exchange, routing_key, commentConsumerRequest);

        return "Send message";
    }

    //Nhận tin nhắn trong hàng đợi và xử lý
    @RabbitListener(queues = "${rabbitmq.queue}")
    public void commentConsumer(CommentConsumerRequest request) {
        var comment = commentMapper.toCommentFromCommentConsumerRequest(request);

        comment.setUserProfile(profileClient.getProfile(request.getUserId()).getResult());
        comment.setLike(0);
        comment.setCreateAt(Instant.now());
        comment.setUpdateAt(Instant.now());

        commentRepository.save(comment);
    }

    //Lấy danh sách comment
    public PageResponse<CommentResponse> getCommentsOfProduct(int page, int size, String productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Sort sort = Sort.by("createAt").descending();
        Pageable pageable = PageRequest.of(Math.max(0, page-1), size, sort);
        var commentData = commentRepository.findAllByProductId(productId, pageable);

        var commentList = commentData.getContent().stream().map(comment -> {
            var commentResponse = commentMapper.toCommentResponse(comment);
            commentResponse.setCreateAt(dateTimeFormatter.format(comment.getCreateAt()));

            if(ObjectUtils.isEmpty(authentication)) {
                commentResponse.setLiked(false);
            } else {
                String comment_likes_pending_key = COMMENT_LIKE_KEY_PREFIX + "pending:" + comment.getId();
                String comment_likes_key = COMMENT_LIKE_KEY_PREFIX + comment.getId();

                //Kiểm tra trong redis key comment_likes_key còn tồn tại không
                if(redisTemplate.opsForSet().size(comment_likes_key) == null) {
                    //Nếu không thì lấy thông tin từ DB và lưu vào với tên key comment_likes_key
                    getDataFromCommentLikeIntoRedis(comment.getId());
                }

                Boolean hasLiked_pending = redisTemplate.opsForSet()
                        .isMember(comment_likes_pending_key, authentication.getName());

                Boolean hasLiked = redisTemplate.opsForSet()
                        .isMember(comment_likes_key, authentication.getName());

                commentResponse.setLiked(
                        Boolean.valueOf(true).equals(hasLiked_pending) || Boolean.valueOf(true).equals(hasLiked)
                );

                Long likeCount_pending = redisTemplate.opsForSet().size(comment_likes_pending_key);
                Long likeCount = redisTemplate.opsForSet().size(comment_likes_key);

                likeCount_pending = likeCount_pending != null ? likeCount_pending : 0;
                likeCount = likeCount != null ? likeCount : 0;

                commentResponse.setLike(likeCount+ likeCount_pending);
            }

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

    public CommentLikeResponse CommentLike(String commentId) {
        //Key có phần value chứa các userId đã like nhưng chưa đồng bộ
        String comment_likes_pending_key = COMMENT_LIKE_KEY_PREFIX + "pending:" + commentId;

        //Key có phần value chứa các userId đã like lấy ra từ DB
        String comment_likes_key = COMMENT_LIKE_KEY_PREFIX + commentId;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        //Kiểm tra trong redis key comment_likes_key còn tồn tại không
        if(redisTemplate.opsForSet().size(comment_likes_key) == null) {
            //Nếu không thì lấy thông tin từ DB và lưu vào với tên key comment_likes_key
            getDataFromCommentLikeIntoRedis(commentId);
        }

        //Kiểm tra user có nằm trong cache (trước khi đồng bộ)
        Boolean hasLiked_pending = redisTemplate.opsForSet()
                .isMember(comment_likes_pending_key, userId);

        Boolean hasLiked = redisTemplate.opsForSet()
                .isMember(comment_likes_key, userId);

        long likeCountDelta = 0;

        //Nếu trong redis không có userId đã like -> thực hiện like
        if(Boolean.valueOf(false).equals(hasLiked) && Boolean.valueOf(false).equals(hasLiked_pending)) {
            redisTemplate.opsForSet().add(comment_likes_pending_key, authentication.getName());

            Long likeCount = redisTemplate.opsForSet().size(comment_likes_pending_key);

            if(likeCount != null) {
                likeCountDelta += likeCount;
            }

        } else if(Boolean.valueOf(true).equals(hasLiked_pending)) {
            //Nếu có thì xoá (người dùng unlike)
            redisTemplate.opsForSet().remove(comment_likes_pending_key, userId);
            likeCountDelta = -1;
        } else if(Boolean.valueOf(true).equals(hasLiked)) {
            //Xoá userId với key comment_likes_key
            redisTemplate.opsForSet().remove(comment_likes_key, userId);
            //Xoá trong DB
            commentLikeRepository.deleteByUserId(userId);
            likeCountDelta = -1;
        }

        return CommentLikeResponse.builder()
                .commentId(commentId)
                .likeCount(likeCountDelta)
                .build();
    }

    //Lấy số lượng like comment trong 1 khoảng thời gian (trước khi redis đồng bộ với DB)
    public CommentLikeResponse getCommentLike(String commentId) {
        //Key có phần value chứa các userId đã like nhưng chưa đồng bộ
        String comment_likes_pending_key = COMMENT_LIKE_KEY_PREFIX + "pending:" + commentId;

        var likeCount = redisTemplate.opsForSet().size(comment_likes_pending_key);

        return CommentLikeResponse.builder()
                .commentId(commentId)
                .likeCount(likeCount != null ? likeCount : 0)
                .build();
    }

    //Lấy danh sách userId đã like từ DB và lưu vào trong redis
    public void getDataFromCommentLikeIntoRedis(String commentId) {
        String comment_likes_key = COMMENT_LIKE_KEY_PREFIX + commentId;

        commentLikeRepository.findUserIdByCommentId(commentId)
                .forEach(userId -> redisTemplate.opsForSet().add(comment_likes_key, userId));
    }

    //Sau 5 phút đồng bộ redis và DB 1 lần
    @Scheduled(fixedRate = 300000)
    public void syncLikeToDB() {
        Set<String> keys = redisTemplate.keys(COMMENT_LIKE_KEY_PREFIX + "pending:" + "*");

        if (ObjectUtils.isEmpty(keys)) return;

        for (String commentLikeKey : keys) {
            String commentId = commentLikeKey.split(":")[3];

            Set<String> userIds = redisTemplate.opsForSet().members(commentLikeKey);

            if (userIds != null) {
                redisTemplate.delete(commentLikeKey);
                //Cập nhật số lượng like trong bảng Comment
                var comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

                //Lưu dữ liệu trong redis vào DB
                userIds.forEach(userId -> {
                    commentLikeRepository.save(CommentLike.builder()
                            .commentId(commentId)
                            .userId(userId)
                            .build());
                });

                comment.setLike(comment.getLike() + userIds.size());

                commentRepository.save(comment);
            }
        }
    }
}
