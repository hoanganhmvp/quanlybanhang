package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_history")
public class StockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id") // Người thực hiện nhập kho
    private User user;

    private Integer quantity;      // Số lượng nhập thêm
    private BigDecimal costPrice;  // Giá vốn tại thời điểm nhập
    private BigDecimal totalAmount; // Tổng tiền nhập (Qty * CostPrice)
    private LocalDateTime importDate;

    public StockHistory() {}

    // GETTERS & SETTERS (Thủ công)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getImportDate() { return importDate; }
    public void setImportDate(LocalDateTime importDate) { this.importDate = importDate; }
}