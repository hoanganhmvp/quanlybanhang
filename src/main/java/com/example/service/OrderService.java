package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.CartItem;
import com.example.entity.Product;
import com.example.entity.SalesDetail;
import com.example.entity.SalesOrder;
import com.example.entity.SalesReturn;
import com.example.entity.User;
import com.example.repository.CartItemRepository;
import com.example.repository.ProductRepository;
import com.example.repository.SalesOrderRepository;
import com.example.repository.SalesReturnRepository;
import com.example.repository.UserRepository;

@Service
public class OrderService {

    @Autowired private SalesOrderRepository orderRepo;
    @Autowired private CartItemRepository cartRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private SalesReturnRepository returnRepo;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Transactional
    public SalesOrder checkout() {
        // Get current user from authentication context
        String currentEmail = getCurrentUserEmail();
        User currentUser = userRepo.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));
        Integer userId = currentUser.getId();
        
        // 1. Lấy toàn bộ giỏ hàng của khách
        List<CartItem> cartItems = cartRepo.findByUserId(userId);
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        // 2. Khởi tạo Đơn hàng (Chung 1 mã hóa đơn)
        SalesOrder order = new SalesOrder();
        order.setUser(currentUser);
        order.setOrderCode("INV-" + System.currentTimeMillis());
        order.setStatus("PENDING"); // Logic: Mặc định là chờ Nhân viên duyệt
        order.setOrderDate(LocalDateTime.now());

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<SalesDetail> details = new ArrayList<>();

        // 3. Duyệt giỏ hàng để tạo Chi tiết hóa đơn
        for (CartItem item : cartItems) {
            Product p = item.getProduct();
            
            // Check tồn kho
            if (p.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + p.getName() + " không đủ hàng!");
            }

            // Trừ tồn kho tạm thời
            int newStock = p.getStock() - item.getQuantity();
            p.setStock(newStock);
            productRepo.save(p);
            // Tạo detail
            SalesDetail detail = new SalesDetail();
            detail.setOrder(order);
            detail.setProduct(p);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(p.getPrice()); 

            BigDecimal subTotal = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(subTotal);
            details.add(detail);
        }

        order.setDetails(details);
        order.setTotalAmount(grandTotal); 

        // 4. Xóa giỏ hàng sau khi đã lên đơn
        cartRepo.deleteByUserId(userId);

        return orderRepo.save(order);
    }

    @Transactional
    public void requestReturn(Integer orderId, Integer productId, Integer qty, String reason) {
        String currentEmail = getCurrentUserEmail();
        
        SalesOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        if (!order.getUser().getEmail().equals(currentEmail)) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không có quyền yêu cầu trả hàng cho hóa đơn của người khác!");
        }
                if (!"PAID".equals(order.getStatus()) && !"DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ được trả hàng khi đơn đã thanh toán hoặc đã giao");
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (qty == null || qty <= 0) {
            throw new RuntimeException("Số lượng trả hàng phải lớn hơn 0");
        }

        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setOrder(order);
        salesReturn.setProduct(product);
        salesReturn.setQuantity(qty);
        salesReturn.setReason(reason);
        salesReturn.setStatus("REQUESTED");
        salesReturn.setCreatedAt(LocalDateTime.now());

        returnRepo.save(salesReturn);

        order.setStatus("PENDING_RETURN");
        orderRepo.save(order);
    }

    @Transactional
    public void confirmReturn(Integer returnId, Integer employeeId) {
        SalesReturn salesReturn = returnRepo.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu trả hàng không tồn tại"));

        if (!"REQUESTED".equals(salesReturn.getStatus())) {
            throw new RuntimeException("Yêu cầu trả hàng không ở trạng thái chờ xử lý");
        }

        User employee = userRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));

        Product product = salesReturn.getProduct();
        product.setStock(product.getStock() + salesReturn.getQuantity());
        productRepo.save(product);

        salesReturn.setStatus("COMPLETED");
        salesReturn.setProcessedBy(employee);
        returnRepo.save(salesReturn);

        SalesOrder order = salesReturn.getOrder();
        order.setStatus("RETURNED");
        orderRepo.save(order);
    }

    @Transactional
    public void confirmPayment(Integer orderId) {
        // Logic: Nhân viên/Admin xác nhận hóa đơn
        SalesOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không thấy đơn hàng"));
        
        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Đơn hàng không ở trạng thái chờ xác nhận");
        }

        order.setStatus("PAID"); 
        orderRepo.save(order);
    }
}