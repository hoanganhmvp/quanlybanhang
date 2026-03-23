package com.example.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Product;
import com.example.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired private ProductService productService;

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Integer id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        productService.delete(id);
        return "Xóa thành công sản phẩm id: " + id;
    }
    @PostMapping("/{id}/import")
public ResponseEntity<?> importStock(
        @PathVariable Integer id,
        @RequestParam Integer qty,
        @RequestParam BigDecimal costPrice) {
    
    String currentEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

    productService.importStock(id, qty, costPrice, currentEmail);
    return ResponseEntity.ok("Nhập kho thành công!");
}
}