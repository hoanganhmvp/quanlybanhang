package com.example.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.SalesOrder;
import com.example.entity.User;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {

    // Tính doanh thu giữa 2 khoảng thời gian
    @Query("SELECT SUM(o.totalAmount) FROM SalesOrder o " +
           "WHERE o.status = 'PAID' AND o.orderDate BETWEEN :start AND :end")
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Đếm số đơn hàng giữa 2 khoảng thời gian
    @Query("SELECT COUNT(o) FROM SalesOrder o " +
           "WHERE o.status = 'PAID' AND o.orderDate BETWEEN :start AND :end")
    Long countOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Tổng doanh thu thanh toán
    @Query("SELECT SUM(o.totalAmount) FROM SalesOrder o WHERE o.status = 'PAID'")
    BigDecimal calculateTotalRevenue();

    // Tổng số đơn hàng thanh toán
    @Query("SELECT COUNT(o) FROM SalesOrder o WHERE o.status = 'PAID'")
    Long countTotalOrders();

    // Lấy lịch sử đơn hàng theo User, mới nhất trước
    List<SalesOrder> findByUserOrderByIdDesc(User user);
    Optional<SalesOrder> findFirstByUserIdOrderByOrderDateDesc(Integer userId);
}