package com.enterprise.iam.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.iam.dto.request.ForgotPasswordRequest;
import com.enterprise.iam.dto.request.LoginRequest;
import com.enterprise.iam.dto.request.RegisterRequest;
import com.enterprise.iam.dto.request.ResetPasswordRequest;
import com.enterprise.iam.dto.request.VerifyOtpRequest;
import com.enterprise.iam.dto.response.ApiResponse;
import com.enterprise.iam.dto.response.JwtResponse;
import com.enterprise.iam.entity.AuditLog;
import com.enterprise.iam.entity.LoginHistory;
import com.enterprise.iam.entity.Otp;
import com.enterprise.iam.entity.Role;
import com.enterprise.iam.entity.User;
import com.enterprise.iam.repository.AuditLogRepository;
import com.enterprise.iam.repository.LoginHistoryRepository;
import com.enterprise.iam.repository.OtpRepository;
import com.enterprise.iam.repository.RoleRepository;
import com.enterprise.iam.repository.UserRepository;
import com.enterprise.iam.security.CustomUserDetails;
import com.enterprise.iam.security.JwtService;
import com.enterprise.iam.service.AuthService;
import com.enterprise.iam.service.OtpService;
import com.enterprise.iam.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private final AuditLogRepository auditLogRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    @Transactional
    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(
                    false,
                    "Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return new ApiResponse(
                    false,
                    "Username already exists");
        }

        Role role = roleRepository
                .findByRoleName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleName("ROLE_USER")
                                .description("Default user role")
                                .build()));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .roles(roles)
                .build();

        userRepository.save(user);

        saveAuditLog(
                user.getUsername(),
                "REGISTER",
                "AUTHENTICATION",
                "User registered successfully");

        return new ApiResponse(
                true,
                "User Registered Successfully");
    }

    @Override
    @Transactional
    public JwtResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsernameOrEmail())
                .orElseGet(() ->
                        userRepository
                                .findByEmail(
                                        request.getUsernameOrEmail())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Invalid username/email or password")));

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        if (user.isAccountLocked()) {
            throw new RuntimeException("User account is locked");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            saveLoginHistory(
                    user.getUsername(),
                    null,
                    false,
                    "Invalid password");

            throw new RuntimeException(
                    "Invalid username/email or password");
        }

        String accessToken =
                jwtService.generateToken(
                        new CustomUserDetails(user));

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        saveLoginHistory(
                user.getUsername(),
                null,
                true,
                null);

        saveAuditLog(
                user.getUsername(),
                "LOGIN",
                "AUTHENTICATION",
                "User logged in successfully");

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return;
        }

        String jwt = token.substring(7);

        try {
            String username =
                    jwtService.extractUsername(jwt);

            userRepository.findByUsername(username)
                    .ifPresent(user -> {
                        refreshTokenService
                                .deleteRefreshTokenForUser(user);

                        saveAuditLog(
                                username,
                                "LOGOUT",
                                "AUTHENTICATION",
                                "User logged out");
                    });

        } catch (Exception ignored) {
            // Logout remains idempotent.
        }
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {

        if (!refreshTokenService
                .validateRefreshToken(refreshToken)) {

            throw new RuntimeException(
                    "Invalid or expired refresh token");
        }

        return refreshTokenService
                .generateNewAccessToken(refreshToken);
    }

    @Override
    public void forgotPassword(
            ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        String otp =
                otpService.generateOtp(user.getEmail());

        /*
         * Email service can be connected here.
         * For development/testing, the OTP is printed
         * in the application console.
         */
        System.out.println(
                "Password reset OTP for "
                        + user.getEmail()
                        + " : "
                        + otp);

        saveAuditLog(
                user.getUsername(),
                "FORGOT_PASSWORD",
                "AUTHENTICATION",
                "Password reset OTP generated");
    }

    @Override
    public void verifyOtp(
            VerifyOtpRequest request) {

        boolean verified =
                otpService.verifyOtp(
                        request.getEmail(),
                        request.getOtp());

        if (!verified) {
            throw new RuntimeException(
                    "OTP verification failed");
        }

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        saveAuditLog(
                user.getUsername(),
                "VERIFY_OTP",
                "AUTHENTICATION",
                "OTP verified successfully");
    }

    @Override
    @Transactional
    public void resetPassword(
            ResetPasswordRequest request) {

        Otp otp = otpRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"));

        if (!otp.isVerified()) {
            throw new RuntimeException(
                    "OTP has not been verified");
        }

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);

        otpService.deleteOtp(request.getEmail());

        saveAuditLog(
                user.getUsername(),
                "RESET_PASSWORD",
                "AUTHENTICATION",
                "Password reset successfully");
    }

    private void saveAuditLog(
            String username,
            String action,
            String module,
            String description) {

        AuditLog auditLog = AuditLog.builder()
                .username(username)
                .action(action)
                .module(module)
                .description(description)
                .build();

        auditLogRepository.save(auditLog);
    }

    private void saveLoginHistory(
            String username,
            String ipAddress,
            boolean successful,
            String failureReason) {

        LoginHistory history = LoginHistory.builder()
                .username(username)
                .ipAddress(ipAddress)
                .successful(successful)
                .failureReason(failureReason)
                .build();

        loginHistoryRepository.save(history);
    }
}