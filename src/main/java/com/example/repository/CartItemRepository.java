package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    java.util.Optional<CartItem> findByUserIdAndProductId(Integer userId, Integer productId);
    List<CartItem> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);
}
