package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;

    // Khách hàng bấm thanh toán giỏ hàng
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout() {
        // Logic tự lấy user từ Token bên trong Service
        return ResponseEntity.ok(orderService.checkout());
    }

    // Nhân viên/Admin xác nhận thanh toán
    @PutMapping("/{id}/paid") // <--- Ở đây bạn dùng PUT
    public String markAsPaid(@PathVariable Integer id) {
        orderService.confirmPayment(id);
        return "Đơn hàng " + id + " đã được chuyển sang trạng thái PAID";
    }
     @PostMapping("/{orderId}/return-request")
    public ResponseEntity<?> requestReturn(
            @PathVariable Integer orderId,
            @RequestParam Integer productId,
            @RequestParam Integer qty,
            @RequestParam String reason) {
        
        orderService.requestReturn(orderId, productId, qty, reason);
        return ResponseEntity.ok("Yêu cầu trả hàng đã được gửi thành công.");
    }

    @PutMapping("/returns/{returnId}/confirm")
    public ResponseEntity<?> confirmReturn(
            @PathVariable Integer returnId,
            @RequestParam Integer employeeId) {
        
        try {
            orderService.confirmReturn(returnId, employeeId);
            return ResponseEntity.ok(Map.of("message", "Xác nhận trả hàng thành công. Sản phẩm đã được hoàn lại vào kho."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}