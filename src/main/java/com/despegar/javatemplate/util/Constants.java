package com.despegar.javatemplate.util;

public final class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String X_CLIENT = "X-Client";
    public static final String X_SERVICE = "X-Service";
    public static final String REQUEST_ID = "requestId";
    public static final String REFERER = "referer";
    public static final String UNKNOWN = "unknown";
    public static final String AUTHORIZATION_HEADER = "Authorization";
}
