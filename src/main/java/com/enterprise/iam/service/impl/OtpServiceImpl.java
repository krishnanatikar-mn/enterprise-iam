package com.enterprise.iam.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

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
    public String generateOtp(String email) {

        otpRepository.findByEmail(email)
                .ifPresent(otpRepository::delete);

        String generatedOtp = otpGenerator.generateOtp();

        Otp otp = Otp.builder()
                .email(email)
                .otp(generatedOtp)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .verified(false)
                .build();

        otpRepository.save(otp);

        return generatedOtp;
    }

    @Override
    public boolean verifyOtp(String email, String otpCode) {

        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        if (!otp.getOtp().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP");
        }

        otp.setVerified(true);

        otpRepository.save(otp);

        return true;
    }

    @Override
    public void deleteOtp(String email) {

        otpRepository.findByEmail(email)
                .ifPresent(otpRepository::delete);

    }

}