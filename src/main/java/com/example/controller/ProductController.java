package com.example.controller;   // 🔥 BẮT BUỘC

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Product;
import com.example.service.ProductService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping
    public Product create(@Valid @RequestBody Product p){
        return service.create(p);
    }

    @GetMapping
    public List<Product> all(){
        return service.getAll();
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Integer id,@RequestBody Product p){
        return service.update(id,p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        service.delete(id);
    }
@PutMapping("/{id}/import")
public void importStock(@PathVariable Integer id,@RequestParam Integer qty){
    service.importStock(id,qty);
}


}
