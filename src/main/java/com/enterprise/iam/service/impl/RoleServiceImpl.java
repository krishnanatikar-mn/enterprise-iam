package com.enterprise.iam.service.impl;

import com.enterprise.iam.dto.request.AssignRoleRequest;
import com.enterprise.iam.dto.request.CreateRoleRequest;
import com.enterprise.iam.dto.response.ApiResponse;
import com.enterprise.iam.dto.response.RoleResponse;
import com.enterprise.iam.entity.Role;
import com.enterprise.iam.repository.RoleRepository;
import com.enterprise.iam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .build();

        return mapToResponse(roleRepository.save(role));
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
                .orElseThrow(() -> new RuntimeException("Role not found"));

        return mapToResponse(role);

    }

    @Override
    public RoleResponse updateRole(Long id, CreateRoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());

        return mapToResponse(roleRepository.save(role));

    }

    @Override
    public void deleteRole(Long id) {

        roleRepository.deleteById(id);

    }

    private RoleResponse mapToResponse(Role role) {

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build();

    }

	@Override
	public ApiResponse assignRole(AssignRoleRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}