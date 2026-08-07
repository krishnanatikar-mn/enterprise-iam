package com.enterprise.iam.service;

import java.util.List;

import com.enterprise.iam.dto.request.RegisterRequest;
import com.enterprise.iam.dto.request.UpdateUserRequest;
import com.enterprise.iam.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(RegisterRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}