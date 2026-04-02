package com.example.dto;

import java.util.Set;

public class UserResponse {
    private final Integer id;
    private final String name;
    private final String email;
    private final String phone;
    private final String address;
    private final Set<String> roles;

    public UserResponse(Integer id, String name, String email, String phone, String address, Set<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.roles = roles;
    }

    // GETTERS (Thủ công)
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Set<String> getRoles() { return roles; }
}