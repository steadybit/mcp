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
class EnvironmentToolsTest {

    private static final WireMockServer wireMock = new WireMockServer(wireMockConfig().dynamicPort());

    @Autowired
    private EnvironmentTools environmentTools;

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
    void getEnvironments() {
        // Given
        var expectedResponse = "TEST_RESPONSE";

        wireMock.stubFor(get(urlPathEqualTo("/environments"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.environmentTools.getEnvironments();

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/environments")));
    }

}