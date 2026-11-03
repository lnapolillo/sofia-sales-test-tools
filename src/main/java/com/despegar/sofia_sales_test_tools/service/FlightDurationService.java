package com.despegar.sofia_sales_test_tools.service;

import com.despegar.sofia_sales_test_tools.model.geo.Airport;
import com.despegar.sofia_sales_test_tools.model.geo.City;
import com.despegar.sofia_sales_test_tools.model.geo.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class FlightDurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightDurationService.class);

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double CRUISE_SPEED_KMH = 800.0;
    private static final double FIXED_OVERHEAD_MIN = 30.0;

    private final GeoService geoService;

    @Autowired
    public FlightDurationService(GeoService geoService) {
        this.geoService = geoService;
    }

    public Duration getDuration(String origin, String destination) {
        Location from = resolveLocation(origin);
        Location to = resolveLocation(destination);
        double distanceKm = haversine(
                from.latitude().doubleValue(), from.longitude().doubleValue(),
                to.latitude().doubleValue(), to.longitude().doubleValue());
        long minutes = Math.round(distanceKm / CRUISE_SPEED_KMH * 60 + FIXED_OVERHEAD_MIN);
        Duration duration = Duration.ofMinutes(minutes);
        LOGGER.info("Flight duration: {} -> {} = {} min (distance={} km)",
                origin, destination, minutes, Math.round(distanceKm));
        return duration;
    }

    private Location resolveLocation(String code) {
        Optional<Location> airport = geoService.getAirportByCode(code)
                .map(Airport::location)
                .filter(FlightDurationService::isValid);
        if (airport.isPresent()) {
            LOGGER.info("Code {} resolved as airport: {}", code, airport.get());
            return airport.get();
        }
        Location city = geoService.getCityByCode(code)
                .map(City::location)
                .filter(FlightDurationService::isValid)
                .orElseThrow(() -> {
                    LOGGER.warn("No airport or city location found for code {}", code);
                    return new IllegalArgumentException("No location found for code: " + code);
                });
        LOGGER.info("Code {} resolved as city: {}", code, city);
        return city;
    }

    private static boolean isValid(Location l) {
        return l != null && l.latitude() != null && l.longitude() != null;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(a));
    }
}
