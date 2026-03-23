package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private ReportService reportService;

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return reportService.getGeneralReport();
    }
}