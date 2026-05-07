package com.authsystem.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authsystem.entity.User;
import com.authsystem.repository.UserRepository;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


// @SecurityScheme(
//     type = SecuritySchemeType.HTTP,
//     name = "bearer-key",
//     description = "authorization with JWT token",
//     scheme = "bearer",
//     bearerFormat = "JWT"
// )
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;
    // @PostMapping("/create")
    // public String createUser(){
    //     User user=User.builder().email("test@email.com").password("hello").build();
    //     userRepository.save(user);
    //     return "User Saved";
    // }

    @GetMapping("/all")
    public List<User> getMethodName() {
        return userRepository.findAll();
    }
    
    @GetMapping("/check")
    public String check () {
        return "Working";
    }
    
}
