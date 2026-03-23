package com.example.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "sales_details")
public class SalesDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore // Tránh vòng lặp vô tận khi trả về JSON
    private SalesOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Transient
    @JsonProperty("productId")
    private Integer productId;

    private Integer quantity;
    private BigDecimal price; // Lưu giá tại thời điểm mua

    public SalesDetail() {}

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public SalesOrder getOrder() { return order; }
    public void setOrder(SalesOrder order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getProductId() {
        if (product != null) {
            return product.getId();
        }
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
        if (productId != null) {
            Product p = new Product();
            p.setId(productId);
            this.product = p;
        }
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}