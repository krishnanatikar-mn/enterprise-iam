package com.enterprise.iam.mapper;

import com.enterprise.iam.dto.response.RoleResponse;
import com.enterprise.iam.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build();

    }

}