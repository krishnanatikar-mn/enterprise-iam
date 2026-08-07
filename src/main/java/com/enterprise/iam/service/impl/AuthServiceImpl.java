package com.enterprise.iam.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.enterprise.iam.dto.request.ForgotPasswordRequest;
import com.enterprise.iam.dto.request.LoginRequest;
import com.enterprise.iam.dto.request.RegisterRequest;
import com.enterprise.iam.security.CustomUserDetails;
import com.enterprise.iam.dto.request.ResetPasswordRequest;
import com.enterprise.iam.dto.request.VerifyOtpRequest;
import com.enterprise.iam.dto.response.ApiResponse;
import com.enterprise.iam.dto.response.JwtResponse;
import com.enterprise.iam.entity.Role;
import com.enterprise.iam.entity.User;
import com.enterprise.iam.repository.RoleRepository;
import com.enterprise.iam.repository.UserRepository;
import com.enterprise.iam.security.JwtService;
import com.enterprise.iam.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return new ApiResponse(false, "Username already exists");
        }

        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .roles(roles)
                .build();

        userRepository.save(user);

        return new ApiResponse(true, "User Registered Successfully");
    }

    @Override
    public JwtResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Username"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        String token = jwtService.generateToken(new CustomUserDetails(user));
        
        return JwtResponse.builder()
                .accessToken(token)
                .refreshToken("")
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void logout(String token) {

        // JWT Logout can be implemented using token blacklist
        // Currently no action required

    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {

        // Will implement after RefreshToken entity & service

        return null;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        // Will implement after OTP module

    }

    @Override
    public void verifyOtp(VerifyOtpRequest request) {

        // Will implement after OTP module

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        // Will implement after OTP module

    }

}