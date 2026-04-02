package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Review;
import com.example.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewService reviewService;

    // Khách hàng gửi đánh giá
    @PostMapping
    public ResponseEntity<?> postReview(@RequestBody Map<String, Object> body) {
        Integer productId = (Integer) body.get("productId");
        Integer rating = (Integer) body.get("rating");
        String comment = (String) body.get("comment");

        try {
            Review review = reviewService.postReview(productId, rating, comment);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Ai cũng có thể xem đánh giá của sản phẩm
    @GetMapping("/product/{productId}")
    public List<Review> getProductReviews(@PathVariable Integer productId) {
        return reviewService.getReviewsByProduct(productId);
    }
}