package com.example.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Boolean active;
    private String code;
    private BigDecimal discountValue;
    private LocalDateTime expiryDate;
    private Integer maxUsage;
    private BigDecimal minOrderAmount;
    private String type;
    private Integer usedCount;

    // --- CHÉP ĐOẠN NÀY VÀO CUỐI FILE ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Boolean getActive() { return active; } // Để khớp với lỗi trong ảnh của bạn
    public void setActive(Boolean active) { this.active = active; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Integer getMaxUsage() { return maxUsage; }
    public void setMaxUsage(Integer maxUsage) { this.maxUsage = maxUsage; }

    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
}