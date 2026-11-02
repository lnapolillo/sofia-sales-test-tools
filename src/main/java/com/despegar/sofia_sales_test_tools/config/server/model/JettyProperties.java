package com.despegar.sofia_sales_test_tools.config.server.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server.jetty", ignoreInvalidFields = true)
public record JettyProperties(Integer port, JettyThreadPoolProperties threadPool) {}
