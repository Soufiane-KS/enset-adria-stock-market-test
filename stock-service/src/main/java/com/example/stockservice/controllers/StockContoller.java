package com.example.stockservice.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stockservice.dto.StockReqDto;
import com.example.stockservice.dto.StockResDto;
import com.example.stockservice.entities.StockMarket;
import com.example.stockservice.repositories.StockRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockContoller {

    private final StockRepository stockRepository;

    @GetMapping("/{id}")
    public StockMarket getCotation(@PathVariable String id) {
        return stockRepository.findById(id).orElse(null);
    }
    
    @GetMapping("/")
    public List<StockMarket> getAllCotation() {
        return stockRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteCotation(@PathVariable String id) {
        stockRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public StockResDto updateCotation(@PathVariable String id, @RequestBody StockReqDto requestDto) {
        StockMarket existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotation non trouvée avec l'id: " + id));
        
        existingStock.setDate(requestDto.getDate());
        existingStock.setOpenValue(requestDto.getOpenValue());
        existingStock.setCloseValue(requestDto.getCloseValue());
        existingStock.setVolume(requestDto.getVolume());
        existingStock.setCompanyId(requestDto.getCompanyId());
        
        StockMarket updatedStock = stockRepository.save(existingStock);
        
        double currentPrice = resolveCurrentPrice(updatedStock.getCompanyId(), updatedStock.getCloseValue());
        
        return mapToResponse(updatedStock, currentPrice);
    }

    @PostMapping("/")
    public StockResDto addCotation(@RequestBody StockReqDto requestDto) {
        StockMarket savedStock = stockRepository.save(
            StockMarket.builder()
                .date(requestDto.getDate())
                .openValue(requestDto.getOpenValue())
                .closeValue(requestDto.getCloseValue())
                .volume(requestDto.getVolume())
                .companyId(requestDto.getCompanyId())
                .build()
        );

        double currentPrice = resolveCurrentPrice(savedStock.getCompanyId(), savedStock.getCloseValue());
        return mapToResponse(savedStock, currentPrice);
    }

    private double resolveCurrentPrice(String companyId, double defaultCloseValue) {
        List<StockMarket> companyStocks = stockRepository.findByCompanyIdOrderByDateDesc(companyId);
        return companyStocks.isEmpty() ? defaultCloseValue : companyStocks.get(0).getCloseValue();
    }

    private StockResDto mapToResponse(StockMarket stock, double currentPrice) {
        return StockResDto.builder()
                .id(stock.getId())
                .date(stock.getDate())
                .openValue(stock.getOpenValue())
                .closeValue(stock.getCloseValue())
                .volume(stock.getVolume())
                .companyId(stock.getCompanyId())
                .currentPrice(currentPrice)
                .build();
    }
}
