package com.example.controller;

import com.example.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TestJwtController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/gen-token")
    public String generate(@RequestParam String name) {
        return jwtUtil.generateToken(name, List.of("ROLE_USER"));
    }
}