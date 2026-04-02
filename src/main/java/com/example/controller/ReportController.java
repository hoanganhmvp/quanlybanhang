package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.RevenueDTO;
import com.example.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private ReportService reportService;

    @GetMapping("/revenue")
    // Chỉ Admin mới được xem doanh thu
    public ResponseEntity<Map<String, RevenueDTO>> getRevenueReport() {
        return ResponseEntity.ok(reportService.getFullRevenueReport());
    }
}