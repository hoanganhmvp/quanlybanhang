package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Voucher;
import com.example.repository.VoucherRepository;

@Service
public class VoucherService {
    @Autowired private VoucherRepository repo;

    public List<Voucher> getAll() {
        return repo.findAll();
    }

    public Voucher create(Voucher v) {
        // Xử lý logic nghiệp vụ tại đây
        if (v.getExpiryDate() == null) {
            v.setExpiryDate(LocalDateTime.now().plusDays(30));
        }
        if (v.getCode() != null) {
            v.setCode(v.getCode().toUpperCase().trim());
        }
        if (v.getUsedCount() == null) {
            v.setUsedCount(0);
        }
        if (v.getActive() == null) {
            v.setActive(true);
        }
        return repo.save(v);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}