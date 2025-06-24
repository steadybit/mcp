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

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExperimentTemplateToolsTest {

    private static final WireMockServer wireMock = new WireMockServer(wireMockConfig().dynamicPort());

    @Autowired
    private ExperimentTemplateTools experimentTemplateTools;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("api.token", () -> "abcdef");
        registry.add("api.url", wireMock::baseUrl);
        registry.add("capabilities.enabled[0]", () -> "CREATE_EXPERIMENT_FROM_TEMPLATE");
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
    void getExperimentTemplates() {
        // Given
        var expectedResponse = "TEST_RESPONSE";

        wireMock.stubFor(get(urlPathEqualTo("/experiments/templates"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.experimentTemplateTools.getExperimentTemplates();

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments/templates")));
    }

    @Test
    void getExperimentTemplate() {
        // Given
        var templateId = UUID.randomUUID();
        var expectedResponse = "TEST_RESPONSE";

        wireMock.stubFor(get(urlPathEqualTo("/experiments/templates/" + templateId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.experimentTemplateTools.getExperimentTemplate(templateId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments/templates/" + templateId)));
    }

    @Test
    void createExperimentFromTemplate() {
        // Given
        var templateId = UUID.randomUUID();
        var environment = "test-env";
        var team = "test-team";
        var placeholders = Map.of("key1", "value1", "key2", "value2");
        var externalId = "external-123";
        var location = "/experiments/test-experiment-key";

        wireMock.stubFor(post(urlPathEqualTo("/experiments/templates/" + templateId + "/experiment-create"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Location", location)
                        .withBody("Created")));

        // When
        var result = this.experimentTemplateTools.createExperimentFromTemplate(templateId, environment, team, placeholders, externalId);

        // Then
        assertThat(result).isEqualTo("Created Experiment: " + location);
        wireMock.verify(postRequestedFor(urlPathEqualTo("/experiments/templates/" + templateId + "/experiment-create"))
                .withRequestBody(matchingJsonPath("$.environment", equalTo(environment)))
                .withRequestBody(matchingJsonPath("$.team", equalTo(team)))
                .withRequestBody(matchingJsonPath("$.externalId", equalTo(externalId))));
    }

    @Test
    void createExperimentFromTemplateWithoutOptionalParameters() {
        // Given
        var templateId = UUID.randomUUID();
        var environment = "test-env";
        var team = "test-team";
        var location = "/experiments/test-experiment-key";

        wireMock.stubFor(post(urlPathEqualTo("/experiments/templates/" + templateId + "/experiment-create"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Location", location)
                        .withBody("Created")));

        // When
        var result = this.experimentTemplateTools.createExperimentFromTemplate(templateId, environment, team, null, null);

        // Then
        assertThat(result).isEqualTo("Created Experiment: " + location);
        wireMock.verify(postRequestedFor(urlPathEqualTo("/experiments/templates/" + templateId + "/experiment-create"))
                .withRequestBody(matchingJsonPath("$.environment", equalTo(environment)))
                .withRequestBody(matchingJsonPath("$.team", equalTo(team))));
    }


}