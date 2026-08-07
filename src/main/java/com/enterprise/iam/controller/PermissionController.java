package com.enterprise.iam.controller;

import com.enterprise.iam.dto.request.CreatePermissionRequest;
import com.enterprise.iam.dto.response.PermissionResponse;
import com.enterprise.iam.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public PermissionResponse createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        return permissionService.createPermission(request);
    }

    @GetMapping
    public List<PermissionResponse> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @GetMapping("/{id}")
    public PermissionResponse getPermission(@PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    @PutMapping("/{id}")
    public PermissionResponse updatePermission(@PathVariable Long id,
                                               @RequestBody CreatePermissionRequest request) {
        return permissionService.updatePermission(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }

}