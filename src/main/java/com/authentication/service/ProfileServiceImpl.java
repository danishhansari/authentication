package com.authentication.service;

import com.authentication.entity.UserEntity;
import com.authentication.io.ProfileRequest;
import com.authentication.io.ProfileResponse;
import com.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements  ProfileService {

    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if (!userRepo.existsByEmail(request.getEmail())) {
            newProfile = userRepo.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .email(newProfile.getEmail())
                .name(newProfile.getName())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .createdAt(newProfile.getCreatedAt())
                .updatedAt(newProfile.getUpdatedAt())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + email));
        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity existingUser = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Generate an Otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));


        // Calculate expiring time
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);

        existingUser.setResetOtp(otp);
        existingUser.setResetOtpExpireAt(expiryTime);

        userRepo.save(existingUser);

        try {
            // TODO send the reset otp email
            emailService.sendResetOtpEmail(email, otp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP is expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepo.save(existingUser);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()) {
            return;
        }

        // Generate 6 digit OTP
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));


        // Calculate expiring time 24 hours
        long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

        userRepo.save(existingUser);
        try {
            // TODO send the reset otp email
            emailService.sendVerifyOtpEmail(existingUser.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepo.save(existingUser);

        try {
            // TODO send the verify otp email
            emailService.sendThankingEmail(existingUser.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email");
        }
    }

}
