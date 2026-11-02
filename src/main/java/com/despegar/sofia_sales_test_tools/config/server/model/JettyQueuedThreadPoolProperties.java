package com.despegar.sofia_sales_test_tools.config.server.model;

import com.despegar.sofia_sales_test_tools.config.thread.model.BlockingQueueProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server.jetty.thread-pool.queued", ignoreInvalidFields = true)
public record JettyQueuedThreadPoolProperties(
    String name,
    int maxThreads,
    int minThreads,
    int idleTimeout,
    boolean useVirtualThreads,
    BlockingQueueProperties queue) {}
