package com.example.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.CartItem;
import com.example.entity.User;
import com.example.repository.CartItemRepository;
import com.example.repository.UserRepository;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired private CartItemRepository cartRepo;
    @Autowired private UserRepository userRepository;

    @PostMapping("/add")
    public CartItem addToCart(@RequestBody CartItem item) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bạn cần đăng nhập để thực hiện thao tác này"));

        item.setUser(currentUser);

        Optional<CartItem> existingItem = cartRepo.findByUserIdAndProductId(
                currentUser.getId(), 
                item.getProduct().getId()
        );

        if (existingItem.isPresent()) {
            CartItem currentItem = existingItem.get();
            currentItem.setQuantity(currentItem.getQuantity() + item.getQuantity());
            return cartRepo.save(currentItem);
        } else {
            return cartRepo.save(item);
        }
    }
@PutMapping("/update-quantity")
public CartItem updateQuantity(@RequestParam Integer cartItemId, @RequestParam Integer newQty) {
    CartItem item = cartRepo.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy món hàng trong giỏ"));
    
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    if (!item.getUser().getEmail().equals(email)) {
        throw new RuntimeException("Bạn không có quyền sửa giỏ hàng này");
    }

    if (newQty <= 0) {
        cartRepo.delete(item);
        return null;
    }
    
    item.setQuantity(newQty);
    return cartRepo.save(item);
}

@DeleteMapping("/remove/{id}")
public String removeFromCart(@PathVariable Integer id) {
    CartItem item = cartRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Món hàng không tồn tại"));
            
    // Kiểm tra chính chủ
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    if (!item.getUser().getEmail().equals(email)) {
        throw new RuntimeException("Bạn không có quyền xóa món hàng này");
    }

    cartRepo.delete(item);
    return "Đã xóa sản phẩm khỏi giỏ hàng";
}





}