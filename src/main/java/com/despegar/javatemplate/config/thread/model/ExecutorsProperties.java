package com.despegar.javatemplate.config.thread.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "thread-pool", ignoreInvalidFields = true)
public record ExecutorsProperties(Map<String, ThreadPoolExecutorProperties> executors) {}
