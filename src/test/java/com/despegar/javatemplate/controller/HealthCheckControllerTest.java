package com.despegar.javatemplate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "server.jetty.port=0",// for random port
        "server.port=0"
})
class HealthCheckControllerTest extends IntegrationTest {

    @Test
    void testHealthCheck() throws Exception {
        this.getMockMvc().perform(get("/health-check")).andExpect(status().isOk());
    }

    @Test
    void postToHealthCheckGet5xxError() throws Exception {
        this.getMockMvc().perform(post("/health-check"))
                .andExpect(status().is5xxServerError());
    }
}