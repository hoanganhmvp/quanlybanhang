package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Category;
import com.example.repository.CategoryRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") 
public class AppController {

    // CHỈ GIỮ LẠI cái này vì bên dưới có dùng để lấy danh mục
    @Autowired private CategoryRepository categoryRepo;

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @PostMapping("/categories")
    public Category addCategory(@RequestBody Category category) {
        return categoryRepo.save(category);
    }
}