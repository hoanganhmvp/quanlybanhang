package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.dto.ProductReviewResponse;
import com.example.entity.Product;
import com.example.entity.Review;
import com.example.entity.User;
import com.example.repository.ProductRepository;
import com.example.repository.ReviewRepository;
import com.example.repository.UserRepository;

@Service
public class ReviewService {

    @Autowired private ReviewRepository reviewRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ProductRepository productRepo;

    public ProductReviewResponse getProductReviewDetails(Integer productId) {
        // 1. Tính điểm trung bình
        Double avg = reviewRepo.getAverageRatingByProductId(productId);
        
        // 2. Lấy tổng số lượt đánh giá
        Long total = reviewRepo.countByProductId(productId);
        
        // 3. Lấy danh sách các bình luận
        List<Review> reviews = reviewRepo.findByProductIdOrderByCreatedAtDesc(productId);
        
        // Trả về đối tượng tổng hợp
        return new ProductReviewResponse(avg, total, reviews);
    }
    public Review postReview(Integer productId, Integer rating, String comment) {
        // 1. Lấy thông tin người dùng từ Token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).get();

        // 2. Kiểm tra xem đã mua hàng chưa (Logic quan trọng nhất)
        boolean purchased = reviewRepo.hasPurchasedProduct(user.getId(), productId);
        if (!purchased) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và thanh toán thành công!");
        }

        // 3. Ràng buộc số sao (1-5)
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Số sao đánh giá phải từ 1 đến 5");
        }

        // 4. Lưu đánh giá
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepo.save(review);
    }

    public List<Review> getReviewsByProduct(Integer productId) {
        return reviewRepo.findByProductIdOrderByCreatedAtDesc(productId);
    }
}