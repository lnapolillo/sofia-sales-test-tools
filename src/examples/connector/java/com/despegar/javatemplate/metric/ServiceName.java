package com.despegar.javatemplate.metric;

public class ServiceName extends com.despegar.javatemplate.model.metric.ServiceName {
    // Geo Controller
    private static final String GEO_CONTROLLER = APP_NAME + "." + "GeoController";
    private static final String GEO_CONTROLLER_COUNTRIES = GEO_CONTROLLER + "." + "countries";
    public static final String GEO_CONTROLLER_COUNTRIES_BY_CONFIG = GEO_CONTROLLER_COUNTRIES + "." + "byConfig";
    public static final String GEO_CONTROLLER_COUNTRIES_BY_CODE = GEO_CONTROLLER_COUNTRIES + "." + "byCode";
    public static final String GEO_CONTROLLER_COUNTRY_BY_ID = GEO_CONTROLLER_COUNTRIES + "." + "byId";

    // Geo Client
    private static final String GEO_CLIENT = APP_NAME + "." + "GeoClient";
    private static final String GEO_CLIENT_COUNTRIES = GEO_CLIENT + "." + "countries";
    public static final String GEO_CLIENT_COUNTRIES_BY_CONFIG = GEO_CLIENT_COUNTRIES + "." + "byConfig";
    public static final String GEO_CLIENT_COUNTRIES_BY_CODE = GEO_CLIENT_COUNTRIES + "." + "byCode";
    public static final String GEO_CLIENT_COUNTRY_BY_ID = GEO_CLIENT_COUNTRIES + "." + "byId";
}
