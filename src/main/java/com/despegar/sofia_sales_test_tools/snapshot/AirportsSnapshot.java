package com.despegar.sofia_sales_test_tools.snapshot;

import com.despegar.sofia_sales_test_tools.model.geo.Airport;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

@Component
public class AirportsSnapshot extends GeoSnapshot<Airport> {
    public AirportsSnapshot() throws Exception {
        super("airports", new TypeReference<>() {}, Airport::code);
    }
}
