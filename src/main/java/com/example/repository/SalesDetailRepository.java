package com.example.repository;
import com.example.entity.SalesDetail;
import com.example.entity.SalesOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesDetailRepository extends JpaRepository<SalesDetail, Integer> {
     List<SalesDetail> findByOrder(SalesOrder order);
}