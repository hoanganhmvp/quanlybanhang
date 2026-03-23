package com.example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_returns")
public class SalesReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private SalesOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity; // Số lượng trả lại
    private String reason;    // Lý do trả hàng
    private String status;    // REQUESTED (Khách đòi trả), COMPLETED (Nhân viên đã duyệt)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy; // Nhân viên xác nhận trả hàng

    public SalesReturn() {}

    // Getters & Setters thủ công
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public SalesOrder getOrder() { return order; }
    public void setOrder(SalesOrder order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getProcessedBy() { return processedBy; }
    public void setProcessedBy(User processedBy) { this.processedBy = processedBy; }
}