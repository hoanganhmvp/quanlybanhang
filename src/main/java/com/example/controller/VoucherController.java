package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Voucher;
import com.example.service.VoucherService;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin("*")
public class VoucherController {

    @Autowired private VoucherService voucherService;

    @PostMapping
    public Voucher create(@RequestBody Voucher v) {
        return voucherService.create(v);
    }

    @GetMapping
    public List<Voucher> getAll() {
        return voucherService.getAll();
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        voucherService.delete(id);
    }
}