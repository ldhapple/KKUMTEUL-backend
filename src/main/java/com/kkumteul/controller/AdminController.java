package com.kkumteul.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    // 관리자 대시보드 접근
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // 관리자 권한이 있는 사용자만 접근 가능
    @GetMapping("/dashboard")
    public String getAdminDashboard() {
        return "Welcome to the Admin Dashboard!";
    }
}