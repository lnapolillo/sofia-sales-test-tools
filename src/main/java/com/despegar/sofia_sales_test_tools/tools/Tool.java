package com.despegar.sofia_sales_test_tools.tools;

import com.despegar.chappie.ToolExecuteRequest;
import com.despegar.chappie.ToolExecuteResponse;

public interface Tool {
    String getName();
    ToolExecuteResponse execute(ToolExecuteRequest request);
}
