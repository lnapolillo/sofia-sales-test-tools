package com.despegar.sofia_sales_test_tools.config.connector.model;

import org.springframework.lang.Nullable;

import java.util.Optional;

public record RequestProperties(@Nullable Integer maxRetries) {
    public Integer maxRetries() {
        return Optional.ofNullable(maxRetries).orElse(ConnectorDefaults.REQUEST_MAX_RETRIES);
    }
}
