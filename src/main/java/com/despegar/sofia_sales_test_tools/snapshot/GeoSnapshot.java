package com.despegar.sofia_sales_test_tools.snapshot;

import com.despegar.sofia_sales_test_tools.config.connector.MapperFactory;
import com.despegar.sofia_sales_test_tools.config.connector.model.JsonFormat;
import com.despegar.sofia_sales_test_tools.config.connector.model.JsonProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.function.Function;

public abstract class GeoSnapshot<T> extends Snapshot<T> {

    private static final ObjectMapper objectMapper = MapperFactory.createMapper(JsonProperties.of(JsonFormat.SNAKE_CASE));

    public GeoSnapshot(String name, TypeReference<List<T>> typeReference, Function<T, String> key) throws Exception {
        super(name, typeReference, key, objectMapper);
    }
}
