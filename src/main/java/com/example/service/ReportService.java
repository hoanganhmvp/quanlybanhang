package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.RevenueDTO;
import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.repository.SalesOrderRepository;

@Service
public class ReportService {

    @Autowired private SalesOrderRepository orderRepo;
    @Autowired private ProductRepository productRepo;

    public Map<String, Object> getGeneralReport() {
        Map<String, Object> report = new HashMap<>();

        BigDecimal revenue = orderRepo.calculateTotalRevenue();
        Long orders = orderRepo.countTotalOrders();
        List<Product> lowStockProducts = productRepo.findByStockLessThan(5);

        report.put("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
        report.put("totalOrders", orders);
        report.put("lowStockAlert", lowStockProducts);
        report.put("timestamp", LocalDateTime.now());

        return report;
    }

    public Map<String, RevenueDTO> getFullRevenueReport() {
        Map<String, RevenueDTO> report = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        report.put("today", calculate(startOfDay, now, "Today"));

        LocalDateTime startOfWeek = now.with(java.time.DayOfWeek.MONDAY).with(LocalTime.MIN);
        report.put("thisWeek", calculate(startOfWeek, now, "This Week"));

        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        report.put("thisMonth", calculate(startOfMonth, now, "This Month"));

        LocalDateTime startOfYear = now.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
        report.put("thisYear", calculate(startOfYear, now, "This Year"));

        return report;
    }

    private RevenueDTO calculate(LocalDateTime start, LocalDateTime end, String label) {
        return new RevenueDTO(
                label,
                orderRepo.sumRevenueBetween(start, end),
                orderRepo.countOrdersBetween(start, end)
        );
    }
}