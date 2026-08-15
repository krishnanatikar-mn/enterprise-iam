package com.enterprise.iam.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.iam.entity.Otp;
import com.enterprise.iam.repository.OtpRepository;
import com.enterprise.iam.service.OtpService;
import com.enterprise.iam.util.OtpGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final OtpGenerator otpGenerator;

    @Override
    @Transactional
    public String generateOtp(String email) {

        otpRepository.findByEmail(email)
                .ifPresent(otpRepository::delete);

        String otpCode = otpGenerator.generateOtp();

        Otp otp = Otp.builder()
                .email(email)
                .otp(otpCode)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .verified(false)
                .build();

        otpRepository.save(otp);

        return otpCode;
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otpCode) {

        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!otp.getOtp().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP");
        }

        otp.setVerified(true);
        otpRepository.save(otp);

        return true;
    }

    @Override
    @Transactional
    public void deleteOtp(String email) {

        otpRepository.findByEmail(email)
                .ifPresent(otpRepository::delete);
    }
}