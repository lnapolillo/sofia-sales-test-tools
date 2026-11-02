package com.despegar.sofia_sales_test_tools.service;

import com.despegar.sofia_sales_test_tools.model.metric.BusinessCounter;
import com.despegar.sofia_sales_test_tools.model.metric.BusinessLabel;
import io.micrometer.core.instrument.Tag;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrometheusService {

    private final PrometheusMeterRegistry registry;

    @Autowired
    public PrometheusService(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    public void count(BusinessCounter counter) {
        count(counter, Map.of(), 1);
    }

    public void count(BusinessCounter counter, Integer incrementAmount) {
        count(counter, Map.of(), incrementAmount);
    }

    public void count(BusinessCounter counter, Map<BusinessLabel, String> tags) {
        count(counter, tags, 1);
    }

    public void count(BusinessCounter counter, Map<BusinessLabel, String> tags, Integer incrementAmount) {
        final var parsedTags = tags.entrySet().stream().map(tag -> Tag.of(tag.getKey().getName(), tag.getValue()))
                .toList();
        registry.counter(counter.getName(), parsedTags).increment(incrementAmount);
    }

}
