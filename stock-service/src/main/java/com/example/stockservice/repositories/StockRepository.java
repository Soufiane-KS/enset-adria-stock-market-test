package com.example.stock_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stock_service.entities.StockMarket;

public interface StockRepository extends JpaRepository<StockMarket, String> {

    // Trouver toutes les cotations d'une entreprise triées par date décroissante
    @Query("SELECT s FROM StockMarket s WHERE s.companyId = :companyId ORDER BY s.date DESC")
    List<StockMarket> findByCompanyIdOrderByDateDesc(@Param("companyId") String companyId);

}
