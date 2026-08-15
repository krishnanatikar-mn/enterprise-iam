package com.enterprise.iam.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.iam.dto.response.JwtResponse;
import com.enterprise.iam.entity.RefreshToken;
import com.enterprise.iam.entity.User;
import com.enterprise.iam.repository.RefreshTokenRepository;
import com.enterprise.iam.security.CustomUserDetails;
import com.enterprise.iam.security.JwtService;
import com.enterprise.iam.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public String createRefreshToken(User user) {

        // Remove any previous refresh token for this user
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {

        return refreshTokenRepository
                .findByToken(refreshToken)
                .filter(token -> !token.isRevoked())
                .filter(token -> token.getExpiryDate() != null)
                .filter(token -> token.getExpiryDate()
                        .isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Override
    public JwtResponse generateNewAccessToken(
            String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new RuntimeException(
                    "Refresh token has been revoked");
        }

        if (storedToken.getExpiryDate() == null ||
                storedToken.getExpiryDate()
                        .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token has expired");
        }

        User user = storedToken.getUser();

        String accessToken = jwtService.generateToken(
                new CustomUserDetails(user));

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void deleteRefreshToken(
            String refreshToken) {

        refreshTokenRepository
                .findByToken(refreshToken)
                .ifPresent(token -> {

                    token.setRevoked(true);

                    refreshTokenRepository.save(token);
                });
    }

    @Override
    @Transactional
    public void deleteRefreshTokenForUser(
            User user) {

        refreshTokenRepository
                .findAll()
                .stream()
                .filter(token ->
                        token.getUser() != null &&
                        token.getUser().getId()
                                .equals(user.getId()))
                .forEach(token -> {

                    token.setRevoked(true);

                    refreshTokenRepository.save(token);
                });
    }
}