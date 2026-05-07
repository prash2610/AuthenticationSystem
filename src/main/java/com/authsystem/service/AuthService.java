package com.authsystem.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authsystem.dto.LoginRequest;
import com.authsystem.dto.RequestRegister;
import com.authsystem.entity.User;
import com.authsystem.repository.UserRepository;
import com.authsystem.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public String register(RequestRegister requestRegister){
        if(userRepository.existsByEmail(requestRegister.getEmail())){
            // throw new RuntimeException("Email already registered");
            return "Email alredy exist";
        }

        String hashedPassword=passwordEncoder.encode(requestRegister.getPassword());

        User user=User.builder().email(requestRegister.getEmail()).password(hashedPassword).build();
        userRepository.save(user);

        return "User registered successfully";
    }

    public Map<String, String> login(LoginRequest loginRequest){
        User user=userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(()->new RuntimeException("User not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new RuntimeException("Invaid credentials");
        }

        String deviceId=UUID.randomUUID().toString();

        String accessToken=jwtUtil.generateToken(user.getEmail());
        String refreshToken=jwtUtil.generateRefreshToken(user.getEmail());
        String key=user.getEmail()+":"+deviceId;
        tokenService.saveRefreshToken(key, refreshToken);

        return Map.of("accesstToken:",accessToken,"refreshToken:",refreshToken,"deviceId",deviceId);
    }

    public Map<String,String> refreshToken(String refreshToken, String deviceId){

        if(!jwtUtil.validateToken(refreshToken)){
            throw new RuntimeException("Invalid refresh token");
        }

        String email =jwtUtil.extractEmail(refreshToken);
        String key=email+":"+deviceId;
        String storedToken = tokenService.getRefreshToken(key);

        if(storedToken==null){
            throw new RuntimeException("Session expired or logged out");
        }

        if( !storedToken.equals(refreshToken)){
            tokenService.deleteAllUserTokens(email);
            throw new RuntimeException("Possible token theft detected");
        }


        String newAccessToken = jwtUtil.generateToken(email);
        String newRefreshToken=jwtUtil.generateRefreshToken(email);

        tokenService.saveRefreshToken(key, newRefreshToken);
        return Map.of("accessToken",newAccessToken,"refreshTokan",newRefreshToken); 
    }

}
