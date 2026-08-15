package com.enterprise.iam.service.impl;

import com.enterprise.iam.dto.request.AssignPermissionRequest;
import com.enterprise.iam.dto.request.AssignRoleRequest;
import com.enterprise.iam.dto.request.CreateRoleRequest;
import com.enterprise.iam.dto.response.ApiResponse;
import com.enterprise.iam.dto.response.RoleResponse;
import com.enterprise.iam.entity.Permission;
import com.enterprise.iam.entity.Role;
import com.enterprise.iam.entity.User;
import com.enterprise.iam.repository.PermissionRepository;
import com.enterprise.iam.repository.RoleRepository;
import com.enterprise.iam.repository.UserRepository;
import com.enterprise.iam.service.RoleService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PermissionRepository permissionRepository;

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {

        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .build();

        Role savedRole = roleRepository.save(role);

        return mapToResponse(savedRole);
    }

    @Override
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        return mapToResponse(role);
    }

    @Override
    public RoleResponse updateRole(
            Long id,
            CreateRoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        role.setRoleName(request.getRoleName());

        role.setDescription(request.getDescription());

        Role updatedRole = roleRepository.save(role);

        return mapToResponse(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {

        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found");
        }

        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ApiResponse assignRole(
            AssignRoleRequest request) {

        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Role role = roleRepository
                .findById(request.getRoleId())
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        user.getRoles().add(role);

        userRepository.save(user);

        return new ApiResponse(
                true,
                "Role assigned successfully");
    }

    @Override
    @Transactional
    public ApiResponse assignPermission(
            AssignPermissionRequest request) {

        Role role = roleRepository
                .findById(request.getRoleId())
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        Permission permission = permissionRepository
                .findById(request.getPermissionId())
                .orElseThrow(() ->
                        new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);

        roleRepository.save(role);

        return new ApiResponse(
                true,
                "Permission assigned to role successfully");
    }

    private RoleResponse mapToResponse(Role role) {

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(
                        role.getPermissions()
                                .stream()
                                .map(Permission::getPermissionName)
                                .collect(Collectors.toSet()))
                .build();
    }
}