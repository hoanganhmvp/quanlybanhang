package com.example.dto;

import java.util.List;

public class UserRoleResponse {
    private final String name;
    private final String email;
    private final List<String> roles;

    public UserRoleResponse(String name, String email, List<String> roles) {
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
}