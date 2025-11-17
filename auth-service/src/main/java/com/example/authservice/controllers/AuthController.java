package com.example.authservice.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.TokenValidationResponse;
import com.example.authservice.services.JwtService;
import com.example.authservice.services.UserService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        if (!userService.validate(request.getUsername(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(request.getUsername());
        Claims claims = jwtService.parseToken(token);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .expiresAt(claims.getExpiration().getTime())
                        .build()
        );
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                    TokenValidationResponse.builder()
                            .valid(false)
                            .message("Missing Bearer token")
                            .build()
            );
        }

        try {
            Claims claims = jwtService.parseToken(authHeader.substring(7));
            return ResponseEntity.ok(
                    TokenValidationResponse.builder()
                            .valid(true)
                            .username(claims.getSubject())
                            .message("Token is valid")
                            .build()
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    TokenValidationResponse.builder()
                            .valid(false)
                            .message("Invalid token: " + ex.getMessage())
                            .build()
            );
        }
    }
}

