package com.example.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        report.put("lowStockAlert", lowStockProducts); // Trả về danh sách sp sắp hết
        report.put("timestamp", java.time.LocalDateTime.now());

        return report;
    }
}