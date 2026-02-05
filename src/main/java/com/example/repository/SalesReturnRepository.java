package com.example.repository;

import com.example.entity.SalesReturn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesReturnRepository extends JpaRepository<SalesReturn, Integer> {
}