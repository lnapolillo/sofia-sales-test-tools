package com.despegar.sofia_sales_test_tools.config.server.factory;

import com.despegar.library.routing.RSD;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadFactory;

public class RsdAwareThreadFactory implements ThreadFactory {
    private final ThreadFactory delegate;

    public RsdAwareThreadFactory(ThreadFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Map<String, Object> parentThreadRsd = RSD.copy();
        Runnable wrapperRunnable = () -> {
            RSD.set(parentThreadRsd);
            try {
                r.run();
            } finally {
                RSD.clear();
            }
        };
        return delegate.newThread(wrapperRunnable);
    }
}