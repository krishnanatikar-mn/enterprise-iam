package com.enterprise.iam.service;

import com.enterprise.iam.dto.request.CreatePermissionRequest;
import com.enterprise.iam.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {

    PermissionResponse createPermission(CreatePermissionRequest request);

    List<PermissionResponse> getAllPermissions();
    PermissionResponse getPermissionById(Long id);

    PermissionResponse updatePermission(Long id,
    CreatePermissionRequest request);

    void deletePermission(Long id);

}