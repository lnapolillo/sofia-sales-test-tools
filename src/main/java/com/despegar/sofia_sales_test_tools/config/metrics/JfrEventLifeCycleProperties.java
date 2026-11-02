package com.despegar.sofia_sales_test_tools.config.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jfr.pinned-vts.lifecycle", ignoreInvalidFields = true)
public record JfrEventLifeCycleProperties(
    Duration streamEventMaxAge) {
}
