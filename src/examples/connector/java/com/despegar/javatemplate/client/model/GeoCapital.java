package com.despegar.javatemplate.client.model;

public class GeoCapital {
    private String id;
    private double offset;

    public GeoCapital() {
    }

    public GeoCapital(String id, double offset) {
        this.id = id;
        this.offset = offset;
    }

    public String getId() {
        return id;
    }

    public double getOffset() {
        return offset;
    }
}
