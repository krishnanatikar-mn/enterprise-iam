package com.enterprise.iam.service;

public interface OtpService {

    String generateOtp(String email);

    boolean verifyOtp(String email, String otp);

    void deleteOtp(String email);

}