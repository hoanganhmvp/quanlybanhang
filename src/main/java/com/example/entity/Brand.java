package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    // Constructor mặc định (Bắt buộc phải có trong Entity)
    public Brand() {}

    // GETTER cho id
    public Integer getId() {
        return id;
    }

    // SETTER cho id
    public void setId(Integer id) {
        this.id = id;
    }

    // GETTER cho name
    public String getName() {
        return name;
    }

    // SETTER cho name
    public void setName(String name) {
        this.name = name;
    }
}