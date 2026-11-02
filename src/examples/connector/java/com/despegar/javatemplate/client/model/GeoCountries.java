package com.despegar.javatemplate.client.model;

import java.util.List;

public class GeoCountries {
    private List<GeoCountry> items;

    public GeoCountries() {
    }

    public GeoCountries(List<GeoCountry> items) {
        this.items = items;
    }

    public List<GeoCountry> getItems() {
        return items;
    }
}
