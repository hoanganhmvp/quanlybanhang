package com.example.dto;

import java.math.BigDecimal;

public class InventoryReport {
    private final String productName;
    private final Integer stock;
    private final BigDecimal currentPrice;

    public InventoryReport(String productName, Integer stock, BigDecimal currentPrice) {
        this.productName = productName;
        this.stock = stock;
        this.currentPrice = currentPrice;
    }

    // Getters & Setters
    public String getProductName() { return productName; }
    public Integer getStock() { return stock; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
}