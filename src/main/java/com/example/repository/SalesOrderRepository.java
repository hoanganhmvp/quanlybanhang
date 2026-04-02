package com.example.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.SalesOrder;
import com.example.entity.User;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {

    List<SalesOrder> findByUserOrderByIdDesc(User user);

    @Query("SELECT SUM(o.totalAmount) FROM SalesOrder o WHERE o.status = 'PAID'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COUNT(o) FROM SalesOrder o WHERE o.status = 'PAID'")
    Long countTotalOrders();
}

