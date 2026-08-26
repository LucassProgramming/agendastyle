package com.agendastyle.backend.catalog;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "services")
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active=true;

    protected SalonService(){}
    public SalonService(String name, String description, Integer durationMinutes, BigDecimal price){
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    public Long getId(){
         return id;
    }
    public String getName(){
         return name;
    }
    public String getDescription(){
         return description;
    }
    public Integer getDurationMinutes(){
        return durationMinutes;
    }
    public BigDecimal getPrice(){
        return price;
    }
    public boolean isActive(){
        return active;
    }
}