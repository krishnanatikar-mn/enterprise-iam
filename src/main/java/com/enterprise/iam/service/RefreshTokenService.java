package com.enterprise.iam.service;

import com.enterprise.iam.dto.response.JwtResponse;
import com.enterprise.iam.entity.User;

public interface RefreshTokenService {

    String createRefreshToken(User user);

    boolean validateRefreshToken(String refreshToken);

    JwtResponse generateNewAccessToken(String refreshToken);

    void deleteRefreshToken(String refreshToken);

    void deleteRefreshTokenForUser(User user);
}