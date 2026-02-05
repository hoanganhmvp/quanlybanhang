package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_info") // Ánh xạ đúng tên bảng trong ảnh của bạn
public class CustomerInfo {
    @Id
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String address;

    // --- Viết tay Getter/Setter để tránh lỗi Lombok ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}