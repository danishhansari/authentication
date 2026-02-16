package com.authentication.service;

import com.authentication.io.ProfileRequest;
import com.authentication.io.ProfileResponse;

public interface ProfileService {

    ProfileResponse getProfile(String email);

    ProfileResponse createProfile(ProfileRequest request);

    void sendResetOtp(String email);

    void resetPassword(String email, String otp, String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email, String otp);

}
