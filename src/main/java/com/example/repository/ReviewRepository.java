package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Integer productId);

    // Kiểm tra xem User đã mua sản phẩm này và đơn hàng đã thanh toán chưa
    @Query("SELECT COUNT(sd) > 0 FROM SalesDetail sd " +
           "JOIN sd.order o " +
           "WHERE o.user.id = :userId AND sd.product.id = :productId AND o.status = 'PAID'")
    boolean hasPurchasedProduct(Integer userId, Integer productId);
}