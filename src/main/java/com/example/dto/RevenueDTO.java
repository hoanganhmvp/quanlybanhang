package com.example.dto;

import java.math.BigDecimal;

public class RevenueDTO {
    private String period; // Tên khoảng thời gian (VD: "2024-03-22")
    private BigDecimal totalRevenue;
    private Long totalOrders;

    public RevenueDTO(String period, BigDecimal totalRevenue, Long totalOrders) {
        this.period = period;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalOrders = totalOrders != null ? totalOrders : 0L;
    }

    // Getters
    public String getPeriod() { return period; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public Long getTotalOrders() { return totalOrders; }
}