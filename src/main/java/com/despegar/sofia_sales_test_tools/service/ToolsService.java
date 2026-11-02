package com.despegar.sofia_sales_test_tools.service;

import com.despegar.chappie.*;
import com.despegar.sofia_sales_test_tools.tools.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Service
public class ToolsService {

    private final Map<String, Tool> toolRegistry;

    public ToolsService(List<Tool> tools) {
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
        return toolRegistry.get(toolName).execute(request);
    }

}
