package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Product;
import com.example.entity.StockHistory;
import com.example.repository.ProductRepository;
import com.example.repository.StockHistoryRepository;
import com.example.repository.UserRepository;
import com.example.entity.User;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;
        @Autowired private StockHistoryRepository stockHistoryRepo;
    @Autowired private UserRepository userRepo;

    public Product create(Product p) {
        return productRepo.save(p);
    }

    public List<Product> getAll() {
        return productRepo.findAll();
    }

    public Product update(Integer id, Product p) {
        Product old = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SP"));

        // Chỉ cần cập nhật các biến chính
        old.setName(p.getName());
        old.setPrice(p.getPrice());
        old.setStock(p.getStock());
        old.setProductCode(p.getProductCode());
        old.setCostPrice(p.getCostPrice());

        return productRepo.save(old);
    }

    public void delete(Integer id) {
        productRepo.deleteById(id);
    }

       @Transactional
    public void importStock(Integer productId, Integer qty) {
        // 1. Tìm sản phẩm
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không thấy sản phẩm"));

        // 2. LẤY THÔNG TIN NGƯỜI ĐANG ĐĂNG NHẬP (Từ Token)
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
 // Sửa dòng 61 từ findByName thành findByEmail
User currentUser = userRepo.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Không thấy người dùng hiện tại"));

        // 3. Cập nhật số lượng tồn kho của sản phẩm
        p.setStock(p.getStock() + qty);
        productRepo.save(p);

        // 4. LƯU LỊCH SỬ NHẬP HÀNG
        StockHistory history = new StockHistory();
        history.setProduct(p);
        history.setUser(currentUser);
        history.setQuantity(qty);
        history.setCostPrice(p.getCostPrice()); // Lấy giá vốn hiện tại của sản phẩm
        
        // Tính tổng giá nhập: costPrice * quantity
        BigDecimal total = p.getCostPrice().multiply(BigDecimal.valueOf(qty));
        history.setTotalAmount(total);
        
        history.setImportDate(LocalDateTime.now()); // Lưu ngày giờ hiện tại

        stockHistoryRepo.save(history);
    }
}