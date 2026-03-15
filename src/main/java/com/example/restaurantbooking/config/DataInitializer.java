package com.example.restaurantbooking.config;

import com.example.restaurantbooking.service.DemoDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final DemoDataService demoDataService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            demoDataService.initializeDemoData();
            log.info("Demoandmed laetud");
        };
    }
}
