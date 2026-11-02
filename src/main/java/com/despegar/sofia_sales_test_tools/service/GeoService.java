package com.despegar.sofia_sales_test_tools.service;

import com.despegar.sofia_sales_test_tools.model.geo.Airport;
import com.despegar.sofia_sales_test_tools.model.geo.City;
import com.despegar.sofia_sales_test_tools.snapshot.AirportsSnapshot;
import com.despegar.sofia_sales_test_tools.snapshot.CitiesSnapshot;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeoService {

    private final AirportsSnapshot airportsSnapshot;
    private final CitiesSnapshot citiesSnapshot;

    public GeoService(AirportsSnapshot airportsSnapshot, CitiesSnapshot citiesSnapshot) {
        this.airportsSnapshot = airportsSnapshot;
        this.citiesSnapshot = citiesSnapshot;
    }

    public Optional<Airport> getAirportByCode(String code) {
        return airportsSnapshot.getBy(code);
    }

    public Optional<City> getCityByCode(String code) {
        return citiesSnapshot.getBy(code);
    }

}
