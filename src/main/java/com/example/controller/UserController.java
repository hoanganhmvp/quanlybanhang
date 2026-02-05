package com.example.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.entity.User;
import com.example.entity.CustomerInfo;
import com.example.repository.UserRepository;
import com.example.repository.CustomerInfoRepository;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private CustomerInfoRepository customerInfoRepo;

    // 1. Xem danh sách khách hàng (Từ bảng riêng customer_info)
    @GetMapping("/customers")
    public List<CustomerInfo> getCustomerInfo() {
        return customerInfoRepo.findAll();
    }

    // 2. Xem TẤT CẢ User (Từ bảng users - Hiện cả mật khẩu)
    @GetMapping("/users")
    public List<User> getAllUsers() {
        // Trả về thẳng danh sách, không xóa password
        return userRepository.findAll();
    }
}