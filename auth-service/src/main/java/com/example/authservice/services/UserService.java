package com.example.authservice.services;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Map<String, String> users = Map.of(
            "admin", "admin123",
            "analyst", "stocks2024",
            "viewer", "password"
    );

    public boolean validate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}

