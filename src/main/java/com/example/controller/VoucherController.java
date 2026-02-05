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

    @PostMapping
    public Voucher create(@RequestBody Voucher v) {
        if (v.getExpiryDate() == null) {
            v.setExpiryDate(LocalDateTime.now().plusDays(30));
        }
        if (v.getCode() != null) {
            v.setCode(v.getCode().toUpperCase());
        }
        return repo.save(v);
    }

    @GetMapping
    public List<Voucher> getAll() {
        return repo.findAll();
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}