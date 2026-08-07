package com.enterprise.iam.controller;

import com.enterprise.iam.dto.request.ForgotPasswordRequest;
import com.enterprise.iam.dto.request.LoginRequest;
import com.enterprise.iam.dto.request.RegisterRequest;
import com.enterprise.iam.dto.request.ResetPasswordRequest;
import com.enterprise.iam.dto.request.VerifyOtpRequest;
import com.enterprise.iam.dto.response.ApiResponse;
import com.enterprise.iam.dto.response.JwtResponse;
import com.enterprise.iam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
    }

    @PostMapping("/refresh")
    public JwtResponse refreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
    }

    @PostMapping("/verify-otp")
    public void verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
    }

}