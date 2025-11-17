package com.example.stockservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stockservice.entities.StockMarket;

public interface StockRepository extends JpaRepository<StockMarket, String> {

    @Query("SELECT s FROM StockMarket s WHERE s.companyId = :companyId ORDER BY s.date DESC")
    List<StockMarket> findByCompanyIdOrderByDateDesc(@Param("companyId") String companyId);

}
