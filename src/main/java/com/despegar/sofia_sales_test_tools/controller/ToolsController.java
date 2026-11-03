package com.despegar.sofia_sales_test_tools.controller;

import com.despegar.chappie.ToolEvaluateRequest;
import com.despegar.chappie.ToolEvaluateResponse;
import com.despegar.chappie.ToolExecuteRequest;
import com.despegar.chappie.ToolExecuteResponse;
import com.despegar.sofia_sales_test_tools.service.ToolsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class ToolsController {

    private final ToolsService toolsService;

    public ToolsController(ToolsService toolsService) {
        this.toolsService = toolsService;
    }

    @PostMapping("/{toolName}/evaluate")
    public ResponseEntity<ToolEvaluateResponse> evaluate(@PathVariable String toolName, @RequestBody ToolEvaluateRequest request) {
        return ResponseEntity.ok(toolsService.evaluate(toolName, request));
    }

    @PostMapping("/{toolName}/execute")
    public ResponseEntity<ToolExecuteResponse> execute(@PathVariable String toolName, @RequestBody ToolExecuteRequest request) {
        return ResponseEntity.ok(toolsService.execute(toolName, request));
    }
}
