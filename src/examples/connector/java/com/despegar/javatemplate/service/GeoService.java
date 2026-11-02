package com.despegar.javatemplate.service;

import com.despegar.javatemplate.client.GeoClient;
import com.despegar.javatemplate.client.model.GeoCountries;
import com.despegar.javatemplate.client.model.GeoCountry;
import com.despegar.javatemplate.config.server.factory.RsdAwareThreadFactory;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GeoService {
    private final ExecutorService executorService;
    private final GeoClient client;

    @Autowired
    public GeoService(GeoClient client) {
        this.client = client;
        this.executorService = Executors.newThreadPerTaskExecutor(
            new RsdAwareThreadFactory(Thread.ofVirtual().name("geo-service-vt-", 0).factory()));
    }

    public GeoCountries getCountriesByConfig() {
        return CompletableFuture.supplyAsync(client::getCountriesByConfig, executorService).join();
    }

    public GeoCountries getCountriesByCode() {
        return CompletableFuture.supplyAsync(client::getCountriesByCode, executorService).join();
    }

    public GeoCountry getCountryById(String id, Optional<Long> timeout) {
        return CompletableFuture.supplyAsync(() -> client.getCountryById(id, timeout), executorService).join();
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}
