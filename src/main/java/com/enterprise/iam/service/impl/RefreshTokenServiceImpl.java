package com.enterprise.iam.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
    public String createRefreshToken(User user) {

        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);

        return token;

    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {

        return refreshTokenRepository.findByToken(refreshToken)

                .filter(token -> token.getExpiryDate().isAfter(LocalDateTime.now()))

                .isPresent();

    }

    @Override
    public JwtResponse generateNewAccessToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)

                .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        String accessToken = jwtService.generateToken(

                new CustomUserDetails(token.getUser()));

        return JwtResponse.builder()

                .accessToken(accessToken)

                .refreshToken(refreshToken)

                .tokenType("Bearer")

                .build();

    }

    @Override
    public void deleteRefreshToken(String refreshToken) {

        refreshTokenRepository.findByToken(refreshToken)

                .ifPresent(refreshTokenRepository::delete);

    }

}