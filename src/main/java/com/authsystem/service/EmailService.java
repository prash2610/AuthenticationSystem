package com.authsystem.service;

import java.time.LocalDateTime;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.authsystem.entity.User;
import com.authsystem.entity.VerificationToken;
import com.authsystem.repository.UserRepository;
import com.authsystem.repository.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public void sendVerificationEmail(String email, String token){
        String verificationLink="http://localhost:8080/auth/verify?token="+token;
        SimpleMailMessage message= new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify Your Email");
        message.setText("Welcome!\n\n"+"click the link below to verify your account:\n\n"+verificationLink+"\n\nThis link expires in 24 hours.");
        mailSender.send(message);

    }
    
    public void verifyEmail(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(()->new RuntimeException("Invalid token"));
        if(verificationToken.getExpiryDateTime().isBefore(LocalDateTime.now())){
            verificationTokenRepository.delete(verificationToken);
            
            throw new RuntimeException("Verification token expired");
        }

        User user=verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }
}
