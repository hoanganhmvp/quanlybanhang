package com.example.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "brand") @Data
public class Brand {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}