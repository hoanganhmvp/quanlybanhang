package com.example.controller;

import com.example.entity.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") 
public class AppController {

    @Autowired private CategoryRepository categoryRepo;
    @Autowired private BrandRepository categoryBrandRepo;
    @Autowired private UserRepository userRepository;
    @Autowired private SalesOrderRepository salesOrderRepo;

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @PostMapping("/categories")
    public Category addCategory(@RequestBody Category category) {
        return categoryRepo.save(category);
    }

}