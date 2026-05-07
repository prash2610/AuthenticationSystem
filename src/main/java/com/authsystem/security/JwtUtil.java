package com.authsystem.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final String SECRET="MySecretKeyMySecretKeyMySecretKey";
    private final Key key=Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email){
        return Jwts.builder().setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+1000*60*15))
        .signWith(key,SignatureAlgorithm.HS256)
        .compact();
    }
    
    public String generateRefreshToken(String email){
        return Jwts.builder().setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+1000L*60*60*24*7))
        .signWith(key,SignatureAlgorithm.HS256)
        .compact();
    }
    
    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token){
        try{
            getClaims(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    }
}
