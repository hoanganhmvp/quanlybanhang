package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.CustomerInfo;

public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Integer> {
}