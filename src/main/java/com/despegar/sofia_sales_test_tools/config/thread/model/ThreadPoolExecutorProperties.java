package com.despegar.sofia_sales_test_tools.config.thread.model;

public record ThreadPoolExecutorProperties(String name,
                                           int corePoolSize,
                                           int maximumPoolSize,
                                           int keepAliveTime,
                                           BlockingQueueProperties queue) {}
