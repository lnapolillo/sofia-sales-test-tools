package com.despegar.sofia_sales_test_tools.config.connector.model;

import com.despegar.library.rest.HttpMethod;
import com.despegar.library.rest.compression.ContentEncoding;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Map;

public record ClientResourceProperties(
    String name,
    HttpMethod method,
    String path,
    @Nullable Duration responseTimeout,
    @Nullable String mediaType,
    @Nullable ContentEncoding encoding,
    @Nullable Map<String, String> headers,
    @Nullable Map<String, String> params) {
}
