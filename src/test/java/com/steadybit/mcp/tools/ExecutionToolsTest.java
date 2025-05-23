/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionToolsTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ExecutionTools executionTools;

    @BeforeEach
    void setUp() {
        this.executionTools = new ExecutionTools(this.restClient);
    }

    @Test
    void testGetExperimentExecutions_withAllParameters() {
        // Given
        List<String> experiments = Arrays.asList("exp1", "exp2");
        List<String> environments = Arrays.asList("prod", "staging");
        List<String> teams = Arrays.asList("team1", "team2");
        List<String> states = Arrays.asList("COMPLETED", "FAILED");
        Instant from = Instant.parse("2025-05-21T13:00:00.000Z");
        Instant to = Instant.parse("2025-05-21T15:00:00.000Z");
        Integer page = 1;
        Integer pageSize = 25;
        String expectedResponse = "{\"executions\": []}";

        // Mock chain setup
        when(this.restClient.post()).thenReturn(this.requestBodyUriSpec);
        when(this.requestBodyUriSpec.uri("/experiments/executions")).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.body(any(Map.class))).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.body(String.class)).thenReturn(expectedResponse);

        // When
        String result = this.executionTools.getExperimentExecutions(
                experiments, environments, teams, states, from, to, page, pageSize);

        // Then
        assertEquals(expectedResponse, result);

        // Verify the request body
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(this.requestBodySpec).body(bodyCaptor.capture());

        Map<String, Object> capturedBody = bodyCaptor.getValue();
        assertEquals(experiments, capturedBody.get("experimentKeys"));
        assertEquals(environments, capturedBody.get("environments"));
        assertEquals(teams, capturedBody.get("teamKeys"));
        assertEquals(states, capturedBody.get("states"));
        assertEquals(from, capturedBody.get("requestedFrom"));
        assertEquals(to, capturedBody.get("requestedTo"));
        assertEquals(1, capturedBody.get("page"));
        assertEquals(25, capturedBody.get("size"));
    }

    @Test
    void testGetExperimentExecution() {
        int executionId = 123;
        String expectedResponse = "{\"id\":123,\"steps\":[]}";

        when(this.restClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri("/experiments/executions/123?fields=steps")).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.body(String.class)).thenReturn(expectedResponse);

        String result = this.executionTools.getExperimentExecution(executionId);
        assertEquals(expectedResponse, result);
    }

}