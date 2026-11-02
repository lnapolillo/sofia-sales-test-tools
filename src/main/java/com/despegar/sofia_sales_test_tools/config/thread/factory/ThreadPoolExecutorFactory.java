package com.despegar.sofia_sales_test_tools.config.thread.factory;

import com.despegar.sofia_sales_test_tools.config.thread.model.ThreadPoolExecutorProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolExecutorFactory {
    private final BlockingQueueFactory blockingQueueFactory;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolExecutorFactory.class);

    @Autowired
    public ThreadPoolExecutorFactory(BlockingQueueFactory blockingQueueFactory) {
        this.blockingQueueFactory = blockingQueueFactory;
    }

    public ThreadPoolExecutor create(ThreadPoolExecutorProperties properties) {
        return new ThreadPoolExecutor(
                properties.corePoolSize(),
                properties.maximumPoolSize(),
                properties.keepAliveTime(),
                TimeUnit.MILLISECONDS,
                blockingQueueFactory.create(properties.queue()),
                getThreadFactory(properties)
        );
    }

    private static ThreadFactory getThreadFactory(ThreadPoolExecutorProperties properties) {
        return new ThreadFactoryBuilder().setNameFormat(properties.name().concat("-thread-%d"))
                .setUncaughtExceptionHandler(
                        (t1, e) -> LOGGER.error("[{}] Error processing event", properties.name(), e)
                )
                .build();
    }
}
