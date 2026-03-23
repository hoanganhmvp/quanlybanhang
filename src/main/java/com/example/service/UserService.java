package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.UserRoleResponse;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<UserRoleResponse> getUserRoleReport() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserRoleResponse(
                        user.getName(),
                        user.getEmail(),
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
