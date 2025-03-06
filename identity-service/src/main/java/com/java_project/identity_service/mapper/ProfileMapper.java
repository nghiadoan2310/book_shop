package com.java_project.identity_service.mapper;

import com.java_project.identity_service.dto.request.ProfileCreationRequest;
import com.java_project.identity_service.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreateRequest(UserCreationRequest request);
}
