package com.example.stock_service.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder
public class StockMarket {

    @Id
 @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Date date;
    private double openValue;
    private double closeValue;
    private double volume;
    private String companyId;
    
}
