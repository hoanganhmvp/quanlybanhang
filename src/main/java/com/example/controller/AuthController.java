package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.User;
import com.example.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    // 1. Đăng ký cho Khách hàng (Công khai)
    @PostMapping("/register-customer")
    public ResponseEntity<?> registerCustomer(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user, "ROLE_USER"));
    }

    // 2. Đăng ký cho Nhân viên (Chỉ Admin mới có quyền gọi API này)
    @PostMapping("/register-employee")
    public ResponseEntity<?> registerEmployee(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user, "ROLE_EMPLOYEE"));
    }

    // 3. Đăng ký Admin (Nội bộ)
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user, "ROLE_ADMIN"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String token = authService.login(payload.get("email"), payload.get("password"));
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
