package com.java_project.identity_service.mapper;

import com.java_project.identity_service.dto.request.RoleRequest;
import com.java_project.identity_service.dto.response.RoleResponse;
import com.java_project.identity_service.entity.Role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true) //Bỏ qua không map trường permission
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
