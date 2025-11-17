package com.example.authservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenValidationResponse {
    boolean valid;
    String username;
    String message;
}

