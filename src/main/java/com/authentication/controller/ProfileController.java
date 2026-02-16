package com.authentication.controller;

import com.authentication.io.ProfileRequest;
import com.authentication.io.ProfileResponse;
import com.authentication.service.EmailService;
import com.authentication.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    private final EmailService emailService;

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name")
                                          String email) {
        return profileService.getProfile(email);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createProfile(@Valid  @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        // TODO send welcome email
        emailService.sendWelcomeEmail(response.getEmail(), response.getName());
        return response;
    }
}
