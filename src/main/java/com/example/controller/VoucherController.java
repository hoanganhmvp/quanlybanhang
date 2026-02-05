package com.example.controller;

import com.example.entity.Voucher;
import com.example.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin("*")
public class VoucherController {

    @Autowired 
    private VoucherRepository repo;

    // API tạo Voucher mới (Dành cho Admin)
    @PostMapping
    public Voucher create(@RequestBody Voucher v) {
        // Nếu không gửi ngày hết hạn, mặc định là 30 ngày kể từ lúc tạo
        if (v.getExpiryDate() == null) {
            v.setExpiryDate(LocalDateTime.now().plusDays(30));
        }
        // Đảm bảo mã voucher luôn viết hoa
        if (v.getCode() != null) {
            v.setCode(v.getCode().toUpperCase());
        }
        return repo.save(v);
    }

    // API xem tất cả các Voucher hiện có
    @GetMapping
    public List<Voucher> getAll() {
        return repo.findAll();
    }
    
    // API xóa Voucher
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}