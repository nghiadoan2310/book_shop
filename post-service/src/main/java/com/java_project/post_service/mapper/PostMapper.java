package com.java_project.post_service.mapper;

import com.java_project.post_service.dto.request.PostRequest;
import com.java_project.post_service.dto.response.PostResponse;
import com.java_project.post_service.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
