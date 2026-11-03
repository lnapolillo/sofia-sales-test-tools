package com.despegar.sofia_sales_test_tools.service;

import com.despegar.chappie.*;
import com.despegar.sofia_sales_test_tools.tools.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Service
public class ToolsService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ToolsService.class);

    private final Map<String, Tool<?>> toolRegistry;

    @Autowired
    public ToolsService(List<Tool<?>> tools) {
        this.toolRegistry = tools.stream().collect(Collectors.toMap(Tool::getName, identity()));
    }

    public ToolEvaluateResponse evaluate(String toolName, ToolEvaluateRequest request) {
        // dummy
        return new ToolEvaluateResponse(
            request.getId(),
                request.getQuery(),
                request.getContext().getConversation().getAttributes(),
                new ToolStatusResult(null, ToolStatusResult.StatusType.SUCCESS)
        );
    }

    public ToolExecuteResponse execute(String toolName, ToolExecuteRequest request) {
        try {
            ChatData chatData = toolRegistry.get(toolName).execute(request);
            return ToolExecuteResponse.builder()
                    .id(request.getId())
                    .attributes(Objects.requireNonNullElse(request.getAttributes(), Map.of()))
                    .customTrackingData(Map.of())
                    .components(List.of())
                    .chainability(ToolExecuteResponse.Chainability.NON_CHAINABLE)
                    .toolStatusResult(ToolStatusResult.builder()
                            .statusType(ToolStatusResult.StatusType.SUCCESS)
                            .build())
                    .chat(chatData)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error executing tool {}: {}", toolName, e.getMessage(), e);
            return ToolExecuteResponse.builder()
                    .id(request.getId())
                    .attributes(Objects.requireNonNullElse(request.getAttributes(), Map.of()))
                    .customTrackingData(Map.of())
                    .components(List.of())
                    .chainability(ToolExecuteResponse.Chainability.NON_CHAINABLE)
                    .toolStatusResult(ToolStatusResult.builder()
                            .statusType(ToolStatusResult.StatusType.SERVER_ERROR)
                            .build())
                    .build();
        }
    }

}
