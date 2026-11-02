package com.despegar.javatemplate.metrics;

import com.despegar.javatemplate.config.metrics.JfrEventLifeCycleProperties;
import jdk.jfr.consumer.RecordingStream;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class JfrEventLifecycle implements SmartLifecycle {
    private static final String JDK_VIRTUAL_THREAD_PINNED = "jdk.VirtualThreadPinned";
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final JfrVirtualThreadPinnedEventHandler virtualThreadPinnedEventHandler;
    private final JfrEventLifeCycleProperties jfrEventLifeCycleProperties;
    private RecordingStream recordingStream;

    public JfrEventLifecycle(
        JfrVirtualThreadPinnedEventHandler virtualThreadPinnedEventHandler,
        JfrEventLifeCycleProperties jfrEventLifeCycleProperties)
    {
        this.virtualThreadPinnedEventHandler = virtualThreadPinnedEventHandler;
        this.jfrEventLifeCycleProperties = jfrEventLifeCycleProperties;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            recordingStream = new RecordingStream();
            recordingStream.enable(JDK_VIRTUAL_THREAD_PINNED).withStackTrace();
            recordingStream.onEvent(JDK_VIRTUAL_THREAD_PINNED, virtualThreadPinnedEventHandler::handle);
            // prevents memory leaks in long-running apps
            recordingStream.setMaxAge(jfrEventLifeCycleProperties.streamEventMaxAge());

            recordingStream.startAsync();
            running.set(true);
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            recordingStream.close();
            running.set(false);
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}