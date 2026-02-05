package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.entity.SalesOrder;
import com.example.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService service;

    @PostMapping
    public SalesOrder create(@RequestBody SalesOrder order, @RequestParam(required = false) String voucherCode) {
        return service.createOrder(order,voucherCode);
    }
@PutMapping("/{id}/cancel")
public void cancel(@PathVariable Integer id){
    service.cancelOrder(id);  
}
@PutMapping("/{id}/paid")
public String markAsPaid(@PathVariable Integer id) {
    service.confirmPaid(id);
    return "Đơn hàng " + id + " đã được chuyển sang trạng thái PAID";
}
@PostMapping("/{id}/return")
public String returnOrder(
    @PathVariable Integer id, 
    @RequestParam Integer productId, 
    @RequestParam Integer qty, 
    @RequestParam String reason
) {
    service.processReturn(id, productId, qty, reason);
    return "Đã xử lý trả hàng và hoàn kho thành công!";
}
}
