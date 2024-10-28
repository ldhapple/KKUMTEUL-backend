package com.kkumteul.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/protected-resource")
    public ResponseEntity<String> getProtectedResource(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Hello, " + username + "! This is a protected resource.");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Hello Admin, " + username + "! This is the admin dashboard.");
    }

}