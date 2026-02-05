package com.example.controller;
import com.example.repository.RoleRepository;
import com.example.security.JwtUtil;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository; 

    @PostMapping("/register-admin")
    public String registerAdmin(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        return saveUserWithRole(user, "ROLE_ADMIN");
    }

    @PostMapping("/register-employee")
    public String registerEmployee(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        return saveUserWithRole(user, "ROLE_EMPLOYEE");
    }

    @PostMapping("/register-customer")
    public String registerCustomer(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        return saveUserWithRole(user, "ROLE_USER");
    }

    private String saveUserWithRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Role " + roleName + " không tồn tại!"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return "Đăng ký " + roleName + " thành công!";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> payload) {
        String email = payload.get("username");
        String password = payload.get("password");
        User user = userRepository.findByEmail(email).orElse(null);
        

 if (user != null) {
        System.out.println(">>> MK nhap vao: " + payload.get("password"));
        System.out.println(">>> MK trong DB: " + user.getPassword());
        
        if (passwordEncoder.matches(payload.get("password"), user.getPassword())) {
            return jwtUtil.generateToken(user.getEmail(), user.getRoles().stream().map(Role::getName).toList());
        }
    }
        return "Sai tài khoản hoặc mật khẩu";
    }
}