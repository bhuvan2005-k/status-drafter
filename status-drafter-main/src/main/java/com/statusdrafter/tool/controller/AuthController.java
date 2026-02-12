package com.statusdrafter.tool.controller;

import com.statusdrafter.tool.dto.SignupRequest;
import com.statusdrafter.tool.dto.SignupResponse;
import com.statusdrafter.tool.entity.User;
import com.statusdrafter.tool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> registerUser(@RequestBody SignupRequest signupRequest) {
        try {
            // Validate input
            if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new SignupResponse(false, "Username is required", null));
            }
            
            if (signupRequest.getEmail() == null || signupRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new SignupResponse(false, "Email is required", null));
            }
            
            if (signupRequest.getPassword() == null || signupRequest.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                    .body(new SignupResponse(false, "Password must be at least 6 characters", null));
            }

            // Register user
            User newUser = userService.registerNewUser(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getFullName(),
                signupRequest.getPassword(),
                signupRequest.getTeamName()
            );

            return ResponseEntity.ok(
                new SignupResponse(true, "Registration successful! Please login.", newUser.getUsername())
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new SignupResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SignupResponse(false, "Registration failed. Please try again.", null));
        }
    }
}
