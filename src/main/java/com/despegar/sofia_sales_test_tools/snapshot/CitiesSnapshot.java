package com.despegar.sofia_sales_test_tools.snapshot;

import com.despegar.sofia_sales_test_tools.model.geo.City;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

@Component
public class CitiesSnapshot extends GeoSnapshot<City> {
    public CitiesSnapshot() throws Exception {
        super("cities", new TypeReference<>() {}, City::code);
    }
}
