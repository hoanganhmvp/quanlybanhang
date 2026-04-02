package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.ReturnedInventory;

public interface ReturnedInventoryRepository extends JpaRepository<ReturnedInventory, Integer> {
}