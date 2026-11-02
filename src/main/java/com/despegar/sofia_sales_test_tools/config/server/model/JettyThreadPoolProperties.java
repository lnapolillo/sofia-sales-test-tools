package com.despegar.sofia_sales_test_tools.config.server.model;

import com.despegar.sofia_sales_test_tools.config.thread.model.ThreadPoolExecutorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server.jetty.thread-pool", ignoreInvalidFields = true)
public record JettyThreadPoolProperties(ThreadPoolExecutorProperties executor, JettyQueuedThreadPoolProperties queued) {}
