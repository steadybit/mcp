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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExperimentToolsTest {

    private static final WireMockServer wireMock = new WireMockServer(wireMockConfig().dynamicPort());

    @Autowired
    private ExperimentTools experimentTools;

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
    void getExperimentDesigns() {
        // Given
        String teamKey = "ADM";
        String expectedResponse = "TEST_RESPONSE";

        wireMock.stubFor(get(urlPathEqualTo("/experiments"))
                .withQueryParam("team", equalTo(teamKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        String result = this.experimentTools.getExperimentDesigns(teamKey);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments"))
                .withQueryParam("team", equalTo(teamKey)));
    }

    @Test
    void getExperimentDesign() {
        // Given
        String expectedResponse = "TEST_RESPONSE";
        String experimentKey = "ADM-123";

        wireMock.stubFor(get(urlEqualTo("/experiments/" + experimentKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        String result = this.experimentTools.getExperimentDesign(experimentKey);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlEqualTo("/experiments/" + experimentKey)));
    }

}