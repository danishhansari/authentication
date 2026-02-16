package com.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to our Platform");
        message.setText("Hello "+ name+ "\n\nThanks for registering with us!\n\nRegards,\n Authentication Team");
        mailSender.send(message);
    }

    public void sendResetOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for resetting your password is " + otp + ". Use this OTP to proceed with resetting your password ");
        mailSender.send(message);
    }

    public void sendVerifyOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Account Verification OTP");
        message.setText("Your OTP is " + otp + ". Verify your account using this OTP ");
        mailSender.send(message);
    }

    public void sendThankingEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Account Verified");
        message.setText("Your Account has been verified.. Letsss GO ");
        mailSender.send(message);
    }
}
