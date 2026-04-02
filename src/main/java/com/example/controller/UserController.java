package com.example.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.UserResponse;
import com.example.entity.CustomerInfo;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.CustomerInfoRepository;
import com.example.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private CustomerInfoRepository customerInfoRepo;

    @GetMapping("/customers")
    public List<CustomerInfo> getCustomerInfo() {
        return customerInfoRepo.findAll();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/me")
public ResponseEntity<?> getMyProfile() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email).get();
    
    UserResponse profile = new UserResponse(
        user.getId(), user.getName(), user.getEmail(), 
        user.getPhone(), user.getAddress(), 
        user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
    );
    return ResponseEntity.ok(profile);
}

}
