package com.despegar.javatemplate.config.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jfr.pinned-vts.handler", ignoreInvalidFields = true)
public record JfrVirtualThreadPinnedEventHandlerProperties(
    int stackTraceMaxDepth,
    boolean reportPrometheusMetrics,
    boolean reportNewRelicMetrics) {
}
