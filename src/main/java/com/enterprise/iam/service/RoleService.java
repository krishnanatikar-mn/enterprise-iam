package com.enterprise.iam.service;

import com.enterprise.iam.dto.request.AssignPermissionRequest;
import com.enterprise.iam.dto.request.AssignRoleRequest;
import com.enterprise.iam.dto.request.CreateRoleRequest;
import com.enterprise.iam.dto.response.ApiResponse;
import com.enterprise.iam.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(CreateRoleRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse updateRole(Long id, CreateRoleRequest request);

    void deleteRole(Long id);

    ApiResponse assignRole(AssignRoleRequest request);

    ApiResponse assignPermission(AssignPermissionRequest request);
}