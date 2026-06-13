package com.authsystem.controller;

import com.authsystem.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.authsystem.dto.LoginRequest;
import com.authsystem.dto.RequestRegister;
import com.authsystem.service.AuthService;
import com.authsystem.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



// @SecurityScheme(
//     type = SecuritySchemeType.HTTP,
//     name = "bearer-key",
//     description = "authorization with JWT token",
//     scheme = "bearer",
//     bearerFormat = "JWT"
// )

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final TokenService tokenService;
    private final AuthService authService;
    private final EmailService emailService;


    @PostMapping("/register")   
    public String register(@Valid @RequestBody RequestRegister requestRegister) {

        return authService.register(requestRegister);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        emailService.verifyEmail(token);
        return "Email verified successfully";
    }
    

    @Operation(summary = "User login", description = "Authenticates user and returns access & refresh tokens")
    @PostMapping("/login")
    public Map<String,String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestParam String refreshToken, @RequestParam String deviceId) {
        return authService.refreshToken(refreshToken,deviceId);
    }
    
    @PostMapping("/logout")
    public String logout(@RequestParam String email, @RequestParam String deviceId) {
        String key=email+":"+deviceId;
        tokenService.deleteRefreshToken(key);
        return "Logged out successfully";
    }
}
