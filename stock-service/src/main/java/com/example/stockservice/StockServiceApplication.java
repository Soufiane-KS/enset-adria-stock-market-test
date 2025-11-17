package com.example.stockservice;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.stockservice.entities.StockMarket;
import com.example.stockservice.repositories.StockRepository;

@SpringBootApplication
public class StockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedStocks(StockRepository stockRepository) {
        return args -> {
            if (stockRepository.count() > 0) {
                return;
            }

            List<String> companies = List.of(
                    "IAM",
                    "BCP",
                    "ATW",
                    "CIH",
                    "ADH"
            );

            Random random = new Random();
            ZoneId zoneId = ZoneId.systemDefault();

            companies.forEach(company -> IntStream.range(0, 5).forEach(index -> {
                double open = 80 + random.nextDouble(40);
                double close = open + (random.nextDouble() * 4 - 2); // +/-2 fluctuation
                double volume = 10000 + random.nextInt(50000);
                Date date = Date.from(LocalDate.now()
                        .minusDays(index)
                        .atStartOfDay(zoneId)
                        .toInstant());

                stockRepository.save(StockMarket.builder()
                        .date(date)
                        .openValue(open)
                        .closeValue(close)
                        .volume(volume)
                        .companyId(company)
                        .build());
            }));
        };
    }
}
