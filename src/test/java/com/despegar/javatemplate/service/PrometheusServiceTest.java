package com.despegar.javatemplate.service;

import com.despegar.javatemplate.model.metric.BusinessCounter;
import com.despegar.javatemplate.model.metric.BusinessLabel;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PrometheusServiceTest {
    @Mock
    private PrometheusMeterRegistry registry;

    @Mock
    private Counter counter;

    @InjectMocks
    private PrometheusService prometheusService;

    private static final BusinessCounter SOME_COUNTER = BusinessCounter.SOME_COUNTER;
    private static final BusinessLabel SOME_LABEL = BusinessLabel.SOME_LABEL;

    @BeforeEach
    void init() {
        when(registry.counter(eq(SOME_COUNTER.getName()), any(Iterable.class))).thenReturn(counter);
    }

    @Test
    void count_withoutLabels() {
        prometheusService.count(SOME_COUNTER);

        verify(registry, only()).counter(SOME_COUNTER.getName(), List.of());
        verify(counter, only()).increment(1);
    }

    @Test
    void count_withLabels() {
        final var labelValue = "someValue";
        final var tags = List.of(Tag.of(SOME_LABEL.getName(), labelValue));

        prometheusService.count(SOME_COUNTER, Map.of(SOME_LABEL, labelValue));

        verify(registry, only()).counter(SOME_COUNTER.getName(), tags);
        verify(counter, only()).increment(1);
    }

    @Test
    void count_withoutLabelsCustomAmount() {
        final var incrementAmount = 10;

        prometheusService.count(SOME_COUNTER, incrementAmount);

        verify(registry, only()).counter(SOME_COUNTER.getName(), List.of());
        verify(counter, only()).increment(incrementAmount);
    }

    @Test
    void count_withLabelsCustomAmount() {
        final var labelValue = "someValue";
        final var tags = List.of(Tag.of(SOME_LABEL.getName(), labelValue));
        final var incrementAmount = 10;

        prometheusService.count(SOME_COUNTER, Map.of(SOME_LABEL, labelValue), incrementAmount);

        verify(registry, only()).counter(SOME_COUNTER.getName(), tags);
        verify(counter, only()).increment(incrementAmount);
    }
}
