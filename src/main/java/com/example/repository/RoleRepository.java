package com.example.repository;

import com.example.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    // Phương thức dùng để tìm kiếm Role theo tên (VD: "ROLE_ADMIN", "ROLE_USER")
    // Trả về Optional để tránh lỗi NullPointerException nếu không tìm thấy
    Optional<Role> findByName(String name);
}