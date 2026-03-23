package com.example.controller;

import com.example.entity.CartItem;
import com.example.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired private CartItemRepository cartRepo;

    @PostMapping("/add")
    public CartItem addToCart(@RequestBody CartItem item) {
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng của User chưa
        Optional<CartItem> existingItem = cartRepo.findByUserIdAndProductId(
                item.getUser().getId(), 
                item.getProduct().getId()
        );

        if (existingItem.isPresent()) {
            // Nếu có rồi thì cộng dồn số lượng
            CartItem currentItem = existingItem.get();
            currentItem.setQuantity(currentItem.getQuantity() + item.getQuantity());
            return cartRepo.save(currentItem);
        } else {
            // Nếu chưa có thì tạo mới
            return cartRepo.save(item);
        }
    }

    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable Integer userId) {
        return cartRepo.findByUserId(userId);
    }
}