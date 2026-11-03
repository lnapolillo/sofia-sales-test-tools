package com.despegar.sofia_sales_test_tools.tools.flightduration;

import com.despegar.chappie.ChatData;
import com.despegar.chappie.EntityData;
import com.despegar.sofia_sales_test_tools.service.FlightDurationService;
import com.despegar.sofia_sales_test_tools.tools.Tool;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class FlightDurationTool extends Tool<FlightDurationRequest> {

    private final FlightDurationService flightDurationService;

    @Autowired
    public FlightDurationTool(FlightDurationService flightDurationService) {
        super("flight-duration", new TypeReference<>() {});
        this.flightDurationService = flightDurationService;
    }

    @Override
    public ChatData execute(FlightDurationRequest request) {
        FlightDurationResponse flightDuration = toFlightDurationResponse(request, flightDurationService.getDuration(request.origin(), request.destination()));
        return ChatData.builder()
                .entities(List.of(
                        EntityData.builder()
                                .id("flight-duration")
                                .type("flight-duration")
                                .value(toJson(flightDuration))
                                .build()
                ))
                .content(List.of())
                .build();
    }

    private FlightDurationResponse toFlightDurationResponse(FlightDurationRequest request, Duration duration) {
        return duration.toHours() < 1
                ? new FlightDurationResponse(request.origin(), request.destination(), duration.toMinutes(), "minutes")
                : new FlightDurationResponse(request.origin(), request.destination(), duration.toHours(),"hours");
    }
}
