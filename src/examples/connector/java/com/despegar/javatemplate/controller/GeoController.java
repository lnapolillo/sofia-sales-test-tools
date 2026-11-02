package com.despegar.javatemplate.controller;

import com.despegar.javatemplate.client.model.GeoCountries;
import com.despegar.javatemplate.client.model.GeoCountry;
import com.despegar.javatemplate.metric.ServiceName;
import com.despegar.javatemplate.service.GeoService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("geo")
public class GeoController {

    private final GeoService service;

    @Autowired
    public GeoController(GeoService service) {
        this.service = service;
    }

    @Timed(percentiles = { 0.5, 0.8, 0.95, 0.99, 0.999 }, histogram = true, value = ServiceName.GEO_CONTROLLER_COUNTRIES_BY_CONFIG)
    @GetMapping("/countries/byConfig")
    public ResponseEntity<GeoCountries> getCountriesByConfig() {
        return ResponseEntity.ok(service.getCountriesByConfig());
    }

    @Timed(percentiles = { 0.5, 0.8, 0.95, 0.99, 0.999 }, histogram = true, value = ServiceName.GEO_CONTROLLER_COUNTRIES_BY_CODE)
    @GetMapping(value = "/countries/byCode")
    public ResponseEntity<GeoCountries> getCountriesByCode() {
        return ResponseEntity.ok(service.getCountriesByCode());
    }

    @Timed(percentiles = { 0.5, 0.8, 0.95, 0.99, 0.999 }, histogram = true, value = ServiceName.GEO_CONTROLLER_COUNTRY_BY_ID)
    @GetMapping(value = "/countries/{id}")
    public ResponseEntity<GeoCountry> getCountryById(
        @PathVariable("id") String id,
        @RequestParam("timeout") Optional<Long> timeout)
    {
        return ResponseEntity.ok(service.getCountryById(id, timeout));
    }
}
