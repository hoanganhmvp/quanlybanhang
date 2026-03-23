package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.CustomerInfo;
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
}
