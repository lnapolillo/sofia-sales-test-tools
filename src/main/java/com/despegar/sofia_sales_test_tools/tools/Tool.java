package com.despegar.sofia_sales_test_tools.tools;

import com.despegar.chappie.ChatData;
import com.despegar.chappie.ToolExecuteRequest;
import com.despegar.sofia_sales_test_tools.config.connector.MapperFactory;
import com.despegar.sofia_sales_test_tools.config.connector.model.JsonFormat;
import com.despegar.sofia_sales_test_tools.config.connector.model.JsonProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Tool<R> {

    private final String name;
    private final TypeReference<R> requestType;

    private final static ObjectMapper objectMapper = MapperFactory.createMapper(JsonProperties.of(JsonFormat.SNAKE_CASE));

    public Tool(String name, TypeReference<R> requestType) {
        this.name = name;
        this.requestType = requestType;
    }

    public ChatData execute(ToolExecuteRequest request) throws Exception {
        R req = objectMapper.readValue(request.getQuery().getParams(), requestType);
        return execute(req);
    }

    protected String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    protected abstract ChatData execute(R request);

    public String getName() {
        return name;
    }
}
