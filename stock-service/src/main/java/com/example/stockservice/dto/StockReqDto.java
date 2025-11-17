package com.example.stock_service.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReqDto {
    
    private Date date;
    private double openValue;
    private double closeValue;
    private double volume;
    private String companyId;
    
}
