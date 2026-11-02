package com.despegar.sofia_sales_test_tools.config.thread.model;

public record BlockingQueueProperties(String type,
                                      ArrayBlockingQueueProperties array,
                                      BlockingArrayQueueProperties jettyArray,
                                      SynchronousQueueProperties synchronous,
                                      LinkedBlockingQueueProperties linked) {}

