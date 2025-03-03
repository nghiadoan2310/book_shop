package com.java_project.identity_service.mapper;

import com.java_project.identity_service.dto.request.UserCreationRequest;
import com.java_project.identity_service.dto.request.UserUpdateRequest;
import com.java_project.identity_service.dto.response.UserResponse;
import com.java_project.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    //Map dữ liệu request tạo user
    User toUser(UserCreationRequest request);

    UserResponse userResponse(User user);

    //Map dữ liệu request update user
    @Mapping(target = "roles", ignore = true) //Không map trường roles
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
