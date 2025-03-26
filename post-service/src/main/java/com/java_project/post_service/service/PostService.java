package com.java_project.post_service.service;

import com.java_project.post_service.dto.PageResponse;
import com.java_project.post_service.dto.request.PostRequest;
import com.java_project.post_service.dto.response.PostResponse;
import com.java_project.post_service.dto.response.UserProfileResponse;
import com.java_project.post_service.entity.Post;
import com.java_project.post_service.mapper.PostMapper;
import com.java_project.post_service.repository.PostRepository;
import com.java_project.post_service.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    ProfileClient profileClient;

    PostMapper postMapper;
    DateTimeFormatter dateTimeFormatter;

    public PostResponse createPost(PostRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Post post = Post.builder()
                .content(request.getContent())
                .userId(authentication.getName())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        post = postRepository.save(post);

        return postMapper.toPostResponse(post);
    }

    //Hàm get những post của chính người dùng có dữ liệu trả về là PageResponse với trường data có kiểu PostResponse
    public PageResponse<PostResponse> getMyPosts(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Lấy giá trị userId ở key subject trong token được truyền vào khi đăng nhập
        String userId = authentication.getName();

        UserProfileResponse userProfile = null;

        try {
            //Get profile trong profile service
            userProfile = profileClient.getProfile(userId).getResult();
        } catch (Exception e) {
            log.error("error while getting user profile", e);
        }

        //Sắp xếp theo trường createdDate theo chiều giảm dần
        Sort sort = Sort.by("createdDate").descending();

        //Lấy dữ liệu trang page - 1 vì chỉ số trang bắt đầu bằng 0
        //size là số phần tử trong 1 trang
        //sort lấy ở trên
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);
        var pageData = postRepository.findAllByUserId(userId, pageable);

        //Lấy username từ profile
        String username = userProfile != null ? userProfile.getUsername() : null;

        var postList = pageData.getContent().stream().map(post -> {
            PostResponse postResponse = postMapper.toPostResponse(post);
            //Set trường create với đầu vào là mốc tạo post
            postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
            //Set trường username
            postResponse.setUsername(username);

            return postResponse;
        }).toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .totalPages(pageData.getTotalPages())
                .pageSize(size)
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }
}
