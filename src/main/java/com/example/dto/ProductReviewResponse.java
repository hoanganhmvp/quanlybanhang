package com.example.dto;

import java.util.List;

import com.example.entity.Review;

public class ProductReviewResponse {
    private final Double averageRating;
    private final Long totalReviews;
    private final List<Review> reviews;

    public ProductReviewResponse(Double averageRating, Long totalReviews, List<Review> reviews) {
        this.averageRating = (averageRating != null) ? Math.round(averageRating * 10.0) / 10.0 : 0.0;
        this.totalReviews = totalReviews;
        this.reviews = reviews;
    }

    // GETTERS
    public Double getAverageRating() { return averageRating; }
    public Long getTotalReviews() { return totalReviews; }
    public List<Review> getReviews() { return reviews; }
}