package com.authentication.service;

public interface EmailService  {

    void sendWelcomeEmail(String toEmail, String name);

    void sendResetOtpEmail(String email, String otp);

    void sendVerifyOtpEmail(String email, String otp);

    void sendThankingEmail(String email);
    }

