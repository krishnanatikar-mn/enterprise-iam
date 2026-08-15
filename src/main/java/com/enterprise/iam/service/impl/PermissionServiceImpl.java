package com.enterprise.iam.service.impl;

import com.enterprise.iam.dto.request.CreatePermissionRequest;
import com.enterprise.iam.dto.response.PermissionResponse;
import com.enterprise.iam.entity.Permission;
import com.enterprise.iam.repository.PermissionRepository;
import com.enterprise.iam.service.PermissionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(
            CreatePermissionRequest request) {

        if (permissionRepository.existsByPermissionName(
                request.getPermissionName())) {

            throw new RuntimeException(
                    "Permission already exists");
        }

        Permission permission = Permission.builder()
                .permissionName(request.getPermissionName())
                .description(request.getDescription())
                .build();

        Permission savedPermission =
                permissionRepository.save(permission);

        return mapToResponse(savedPermission);
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {

        return permissionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse getPermissionById(Long id) {

        Permission permission =
                permissionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Permission not found"));

        return mapToResponse(permission);
    }

    @Override
    public PermissionResponse updatePermission(
            Long id,
            CreatePermissionRequest request) {

        Permission permission =
                permissionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Permission not found"));

        permission.setPermissionName(
                request.getPermissionName());

        permission.setDescription(
                request.getDescription());

        Permission updatedPermission =
                permissionRepository.save(permission);

        return mapToResponse(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {

        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException(
                    "Permission not found");
        }

        permissionRepository.deleteById(id);
    }

    private PermissionResponse mapToResponse(
            Permission permission) {

        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionName(
                        permission.getPermissionName())
                .description(
                        permission.getDescription())
                .build();
    }
}