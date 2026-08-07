package com.enterprise.iam.mapper;

import com.enterprise.iam.dto.response.PermissionResponse;
import com.enterprise.iam.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(Permission permission) {

        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .build();

    }

}