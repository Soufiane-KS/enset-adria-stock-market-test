package com.example.stockservice.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.stockservice.dto.StockReqDto;
import com.example.stockservice.dto.StockResDto;
import com.example.stockservice.entities.StockMarket;
import com.example.stockservice.repositories.StockRepository;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/stocks")
public class StockContoller {

    StockRepository stockRepository;


    @GetMapping("/{id}")
    public StockMarket getCotation(@RequestParam String id) {
        return stockRepository.findById(id).orElse(null);
    }
    

    @GetMapping("/")
    public List<StockMarket> getAllCotation() {
        return stockRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteCotation(@RequestParam String id) {
        stockRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public StockResDto updateCotation(@PathVariable String id, @RequestBody StockReqDto requestDto) {
        // Récupérer la cotation existante
        StockMarket existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotation non trouvée avec l'id: " + id));
        
        existingStock.setDate(requestDto.getDate());
        existingStock.setOpenValue(requestDto.getOpenValue());
        existingStock.setCloseValue(requestDto.getCloseValue());
        existingStock.setVolume(requestDto.getVolume());
        existingStock.setCompanyId(requestDto.getCompanyId());
        
        StockMarket updatedStock = stockRepository.save(existingStock);
        
        List<StockMarket> companyStocks = stockRepository.findByCompanyIdOrderByDateDesc(requestDto.getCompanyId());
        double currentPrice = companyStocks.isEmpty() ? updatedStock.getCloseValue() : companyStocks.get(0).getCloseValue();
        
        return StockResDto.builder()
                .id(updatedStock.getId())
                .date(updatedStock.getDate())
                .openValue(updatedStock.getOpenValue())
                .closeValue(updatedStock.getCloseValue())
                .volume(updatedStock.getVolume())
                .companyId(updatedStock.getCompanyId())
                .currentPrice(currentPrice)
                .build();
    }

    @PostMapping("/")
    public StockResDto addCotation(@RequestBody StockResDto entity) {
        
        stockRepository.save(
            StockMarket.builder()
                .date(entity.getDate())
                .openValue(entity.getOpenValue())
                .closeValue(entity.getCloseValue())
                .volume(entity.getVolume())
                .companyId(entity.getCompanyId())
                .build()
        );
        return entity;
    }


}
