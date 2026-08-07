package com.enterprise.iam.service;

import com.enterprise.iam.dto.request.*;
import com.enterprise.iam.dto.response.ApiResponse;
import com.enterprise.iam.dto.response.JwtResponse;

public interface AuthService {

    ApiResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);

    void logout(String token);

    JwtResponse refreshToken(String refreshToken);

    void forgotPassword(ForgotPasswordRequest request);

    void verifyOtp(VerifyOtpRequest request);

    void resetPassword(ResetPasswordRequest request);

}