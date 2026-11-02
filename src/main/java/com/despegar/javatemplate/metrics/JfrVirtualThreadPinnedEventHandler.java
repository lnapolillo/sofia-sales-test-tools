package com.despegar.javatemplate.metrics;

import com.despegar.javatemplate.config.metrics.JfrVirtualThreadPinnedEventHandlerProperties;
import com.google.common.collect.ImmutableMap;
import com.newrelic.api.agent.NewRelic;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordedThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class JfrVirtualThreadPinnedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JfrVirtualThreadPinnedEventHandler.class);
    private static final String UNKNOWN_THREAD_NAME = "<unknown>";
    private static final Pattern DESPEGAR_VT_PATTERN = Pattern.compile("(.+)-vt-\\d+");
    private final MeterRegistry meterRegistry;
    private final JfrVirtualThreadPinnedEventHandlerProperties properties;

    JfrVirtualThreadPinnedEventHandler(
            MeterRegistry meterRegistry,
            JfrVirtualThreadPinnedEventHandlerProperties properties)
    {
        this.meterRegistry = meterRegistry;
        this.properties = properties;
    }

    void handle(RecordedEvent event) {
        // marked as nullable in Javadoc
        String threadName = Optional.ofNullable(event.getThread())
                .map(RecordedThread::getJavaName)
                .orElse(UNKNOWN_THREAD_NAME);
        Duration duration = event.getDuration();
        LocalDateTime startTime = LocalDateTime.ofInstant(event.getStartTime(), ZoneId.systemDefault());
        String stackTrace = formatStackTrace(event.getStackTrace(), properties.stackTraceMaxDepth());
        int eventAggregatorHashCode = stackTrace.hashCode();

        LOGGER.warn(
                "EventAggregator: {}: Thread '{}' pinned for: {}ms at {}, stacktrace: {}",
                eventAggregatorHashCode,
                threadName,
                duration.toMillis(),
                startTime,
                stackTrace);

        if (properties.reportPrometheusMetrics()) {
            recordPrometheusMetric(duration);
        }

        if (properties.reportNewRelicMetrics()) {
            recordNewRelicEvent(eventAggregatorHashCode, threadName, duration, startTime);
        }
    }

    private void recordNewRelicEvent(
            int eventAggregatorHashCode,
            String threadName,
            Duration duration,
            LocalDateTime startTime)
    {
        ImmutableMap.Builder<String, Object> eventAttributes = ImmutableMap.builder();
        eventAttributes.put("eventAggregator", eventAggregatorHashCode)
                .put("threadName", threadName)
                .put("threadGroup", extractThreadGroup(threadName, eventAggregatorHashCode))
                .put("durationMsecs", duration.toMillis())
                .put("startTime", startTime);
        NewRelic.getAgent().getInsights().recordCustomEvent("PinnedThread", eventAttributes.build());
    }

    private String extractThreadGroup(String threadName, int eventAggregatorHashCode) {
        Matcher matcher = DESPEGAR_VT_PATTERN.matcher(threadName);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return String.valueOf(eventAggregatorHashCode);
    }

    private void recordPrometheusMetric(Duration duration) {
        Timer timer = meterRegistry.timer("jfr.thread.pinning");
        timer.record(duration);
    }

    private String formatStackTrace(RecordedStackTrace stackTrace, int maxDepth) {
        if (stackTrace == null) {
            return "\t<not available>";
        }
        String formattedStackTrace = stackTrace.getFrames()
                .stream()
                .limit(maxDepth)
                .map(this::formatStackTraceFrame)
                .collect(Collectors.joining("\t"));
        String formatted = String.format("\t%s", formattedStackTrace);
        if (maxDepth < stackTrace.getFrames().size()) {
            return formatted + "\t(...)"; // truncated
        }
        return formatted;
    }

    private String formatStackTraceFrame(RecordedFrame frame) {
        return frame.getMethod().getType().getName() + "#" + frame.getMethod().getName() + ": " + frame.getLineNumber();
    }
}