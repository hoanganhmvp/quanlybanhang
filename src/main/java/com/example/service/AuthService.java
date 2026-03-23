package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.CustomerInfo;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.CustomerInfoRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.security.JwtUtil;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CustomerInfoRepository customerInfoRepo;

    @Transactional
    public String register(User user, String roleName) {
         if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Lỗi: Email không được để trống!");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Lỗi: Mật khẩu không được để trống!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new RuntimeException("Lỗi: Họ và tên không được để trống!");
        }   
        // 1. Check trùng Email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Lỗi: Email này đã được đăng ký!");
        }
        // 2. Check trùng Số điện thoại
        String phone = user.getPhone();

        if (phone != null && !phone.isBlank()) {
            // Nếu có nhập SĐT -> Kiểm tra trùng lặp
            if (userRepository.existsByPhone(phone)) {
                throw new RuntimeException("Lỗi: Số điện thoại này đã được đăng ký!");
            }
        } else {
            // Nếu KHÔNG nhập SĐT -> Chỉ bắt lỗi nếu là Khách hàng
            if ("ROLE_USER".equals(roleName)) {
                throw new RuntimeException("Lỗi: Đăng ký Khách hàng bắt buộc phải có số điện thoại!");
            }
            // Admin và Nhân viên không có SĐT thì gán là null để tránh trùng chuỗi rỗng ""
            user.setPhone(null);
        }

        // 3. Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. Gán quyền tương ứng (ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_USER)
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Lỗi: Quyền hệ thống chưa được khởi tạo!"));
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        if ("ROLE_USER".equalsIgnoreCase(roleName)) {
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setId(savedUser.getId()); // Dùng chung ID
            customerInfo.setName(savedUser.getName());
            customerInfo.setEmail(savedUser.getEmail());
            customerInfo.setPhone(savedUser.getPhone());
            customerInfo.setAddress(savedUser.getAddress());

            customerInfoRepo.save(customerInfo);
            System.out.println(">>> Đã đồng bộ khách hàng: " + customerInfo.getName());
        }

        return "Đăng ký thành công vai trò: " + roleName;
    }

    public String login(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Lỗi: Vui lòng nhập email!");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("Lỗi: Vui lòng nhập mật khẩu!");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không chính xác");
        }

        // Lấy danh sách quyền của User để đưa vào JWT Token
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        // Token này sẽ mang theo thông tin phân quyền
        return jwtUtil.generateToken(user.getEmail(), roles);
    }
}