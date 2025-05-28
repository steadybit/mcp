/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Instant;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExecutionToolsTest {

    private static final WireMockServer wireMock = new WireMockServer(wireMockConfig().dynamicPort());

    @Autowired
    private ExecutionTools executionTools;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("api.token", () -> "abcdef");
        registry.add("api.url", wireMock::baseUrl);
    }

    @BeforeAll
    static void beforeAll() {
        wireMock.start();
    }

    @AfterAll
    static void afterAll() {
        wireMock.stop();
    }

    @BeforeEach
    void setUp() {
        wireMock.resetAll();
    }

    @Test
    void getExperimentExecutions() {
        // Given
        String expectedResponse = "TEST_RESPONSE";

        wireMock.stubFor(post(urlPathEqualTo("/experiments/executions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        String result = this.executionTools.getExperimentExecutions(null, null, null, null, null, null, null, null);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(postRequestedFor(urlPathEqualTo("/experiments/executions")));
    }

    @Test
    void getExperimentExecutionsWithFilters() {
        // Given
        String expectedResponse = "{}";
        List<String> experiments = List.of("ADM-123");
        List<String> environments = List.of("prod");
        List<String> teams = List.of("ADM");
        List<String> states = List.of("COMPLETED");
        Instant from = Instant.parse("2025-05-21T13:00:00.000Z");
        Instant to = Instant.parse("2025-05-21T15:00:00.000Z");
        Integer page = 1;
        Integer pageSize = 25;

        wireMock.stubFor(post(urlPathEqualTo("/experiments/executions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        String result = this.executionTools.getExperimentExecutions(experiments, environments, teams, states, from, to, page, pageSize);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(postRequestedFor(urlPathEqualTo("/experiments/executions"))
                .withRequestBody(containing("\"experimentKeys\":[\"ADM-123\"]"))
                .withRequestBody(containing("\"environments\":[\"prod\"]"))
                .withRequestBody(containing("\"teamKeys\":[\"ADM\"]"))
                .withRequestBody(containing("\"states\":[\"COMPLETED\"]"))
                .withRequestBody(containing("\"requestedFrom\":1747832400.000000000"))
                .withRequestBody(containing("\"requestedTo\":1747839600.000000000"))
                .withRequestBody(containing("\"page\":1"))
                .withRequestBody(containing("\"size\":25")));
    }

    @Test
    void getExperimentExecution() {
        // Given
        Integer executionId = 12345;
        String expectedResponse = "TEST_RESPONSE";

        wireMock.stubFor(get(urlEqualTo("/experiments/executions/" + executionId + "?fields=steps"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        String result = this.executionTools.getExperimentExecution(executionId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlEqualTo("/experiments/executions/" + executionId + "?fields=steps")));
    }

}