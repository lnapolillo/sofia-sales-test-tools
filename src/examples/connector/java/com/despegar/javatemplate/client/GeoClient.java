package com.despegar.javatemplate.client;

import com.despegar.javatemplate.client.model.GeoCountries;
import com.despegar.javatemplate.client.model.GeoCountry;
import com.despegar.javatemplate.config.connector.Connector;
import com.despegar.javatemplate.metric.ServiceName;
import com.despegar.library.rest.compression.ContentEncoding;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class GeoClient {
    private final Connector connector;

    @Autowired
    public GeoClient(@Qualifier("geoConnector") Connector connector) {
        this.connector = connector;
    }

    @Timed(percentiles = { 0.5, 0.8, 0.95, 0.99, 0.999 }, histogram = true, value = ServiceName.GEO_CLIENT_COUNTRIES_BY_CONFIG)
    public GeoCountries getCountriesByConfig() {
        return connector.ofResource("countries")
            .execute(GeoCountries.class);
    }

    @Timed(percentiles = { 0.5, 0.8, 0.95, 0.99, 0.999 }, histogram = true, value = ServiceName.GEO_CLIENT_COUNTRIES_BY_CODE)
    public GeoCountries getCountriesByCode() {
        return connector.get("/v3/countries")
            .param("product", "FLIGHTS")
            .encoding(ContentEncoding.GZIP)
            .mediaType("application/json")
            .execute(GeoCountries.class);
    }

    @Timed(percentiles = { 0.5, 0.8, 0.95, 0.99, 0.999 }, histogram = true, value = ServiceName.GEO_CLIENT_COUNTRY_BY_ID)
    public GeoCountry getCountryById(String id, Optional<Long> timeout) {
        return connector.ofResource("country-by-id")
            .pathVariable("id", id)
            .param("product", "FLIGHTS")
            .encoding(ContentEncoding.GZIP)
            .mediaType("application/json")
            .responseTimeout(Duration.ofMillis(timeout.orElse(0L)))
            .execute(GeoCountry.class);
    }
}
