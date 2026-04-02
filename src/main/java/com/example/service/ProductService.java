package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Product;
import com.example.entity.StockHistory;
import com.example.entity.User;
import com.example.repository.ProductRepository;
import com.example.repository.StockHistoryRepository;
import com.example.repository.UserRepository;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepo;
    @Autowired private StockHistoryRepository stockHistoryRepo;
    @Autowired private UserRepository userRepo;

    public List<Product> getAll() {
        return productRepo.findAll();
    }

    public Product save(Product product) {
        return productRepo.save(product);
    }

    public Product update(Integer id, Product newProduct) {
        Product oldProduct = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id: " + id));
        
        oldProduct.setName(newProduct.getName());
        oldProduct.setPrice(newProduct.getPrice());
        oldProduct.setCostPrice(newProduct.getCostPrice());
        oldProduct.setProductCode(newProduct.getProductCode());
        oldProduct.setCategory(newProduct.getCategory());
        
        return productRepo.save(oldProduct);
    }

    public void delete(Integer id) {
        productRepo.deleteById(id);
    }

    @Transactional
    public void importStock(Integer productId, Integer qty, BigDecimal newCostPrice, String userEmail) {
        // 1. Tìm sản phẩm
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // 2. Tìm người thực hiện (nhân viên/admin đang đăng nhập)
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // 3. Cập nhật bảng Product
        p.setStock(p.getStock() + qty);
        p.setCostPrice(newCostPrice);
        productRepo.save(p);

        // 4. Lưu lịch sử nhập kho
        StockHistory history = new StockHistory();
        history.setProduct(p);
        history.setUser(user);
        history.setQuantity(qty);
        history.setCostPrice(newCostPrice);
        history.setTotalAmount(newCostPrice.multiply(BigDecimal.valueOf(qty)));
        history.setImportDate(LocalDateTime.now());

        stockHistoryRepo.save(history);
    }
}