package com.despegar.sofia_sales_test_tools.snapshot;

import com.despegar.sofia_sales_test_tools.config.connector.MapperFactory;
import com.despegar.sofia_sales_test_tools.config.connector.model.JsonFormat;
import com.despegar.sofia_sales_test_tools.config.connector.model.JsonProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public abstract class Snapshot<T> {
    private final Map<String, T> indexedContent;

    public Snapshot(String name, TypeReference<List<T>> typeReference, Function<T, String> key, ObjectMapper objectMapper) throws Exception {
        List<T> content = objectMapper.readValue(getClass().getResourceAsStream("/%s.json".formatted(name)), typeReference);
        this.indexedContent = content.stream().collect(toMap(key, identity()));
    }

    public Optional<T> getBy(String key) {
        return Optional.ofNullable(indexedContent.get(key));
    }

}
