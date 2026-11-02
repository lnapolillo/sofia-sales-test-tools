package com.despegar.sofia_sales_test_tools.tools;

import com.despegar.chappie.ToolExecuteRequest;
import com.despegar.chappie.ToolExecuteResponse;
import com.despegar.sofia_sales_test_tools.service.FlightDurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FlightDurationTool implements Tool {

    private final FlightDurationService flightDurationService;

    @Autowired
    public FlightDurationTool(FlightDurationService flightDurationService) {
        this.flightDurationService = flightDurationService;
    }

    @Override
    public String getName() {
        return "flight-duration-tool";
    }

    @Override
    public ToolExecuteResponse execute(ToolExecuteRequest request) {
        // TODO
        return null;
    }
}
