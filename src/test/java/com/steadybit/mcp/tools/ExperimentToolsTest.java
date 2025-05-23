/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperimentToolsTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ExperimentTools experimentTools;

    @BeforeEach
    void setUp() {
        this.experimentTools = new ExperimentTools(this.restClient);
    }

    @Test
    void getExperimentDesigns_withTeam_shouldIncludeTeamParameter() {
        // Given
        String expectedResponse = "[{\"key\":\"exp1\",\"name\":\"Test Experiment\"}]";
        String teamKey = "team-alpha";

        when(this.restClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(any(Function.class))).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.body(String.class)).thenReturn(expectedResponse);

        // When
        String result = this.experimentTools.getExperimentDesigns(teamKey);

        // Then
        assertEquals(expectedResponse, result);
        verify(this.restClient).get();
        verify(this.requestHeadersUriSpec).uri(any(Function.class));
        verify(this.requestHeadersUriSpec).retrieve();
        verify(this.responseSpec).body(String.class);
    }


    @Test
    void getExperimentDesign_shouldCallCorrectEndpoint() {
        // Given
        String experimentKey = "chaos-experiment-1";
        String expectedResponse = "{\"key\":\"chaos-experiment-1\",\"name\":\"CPU Stress Test\"}";

        when(this.restClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri("/experiments/" + experimentKey)).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.body(String.class)).thenReturn(expectedResponse);

        // When
        String result = this.experimentTools.getExperimentDesign(experimentKey);

        // Then
        assertEquals(expectedResponse, result);
        verify(this.restClient).get();
        verify(this.requestHeadersUriSpec).uri("/experiments/" + experimentKey);
        verify(this.requestHeadersUriSpec).retrieve();
        verify(this.responseSpec).body(String.class);
    }
}