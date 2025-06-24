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

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExperimentScheduleToolsTest {

    private static final WireMockServer wireMock = new WireMockServer(wireMockConfig().dynamicPort());

    @Autowired
    private ExperimentScheduleTools experimentScheduleTools;

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
    void getExperimentSchedulesWithoutFilters() {
        // Given
        var expectedResponse = "TEST_RESPONSE";

        wireMock.stubFor(get(urlPathEqualTo("/experiments/schedules/v2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.experimentScheduleTools.getExperimentSchedules(null, null);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments/schedules/v2")));
    }

    @Test
    void getExperimentSchedulesWithExperimentFilter() {
        // Given
        var expectedResponse = "TEST_RESPONSE";
        var experiments = List.of("exp1", "exp2");

        wireMock.stubFor(get(urlPathEqualTo("/experiments/schedules/v2"))
                .withQueryParam("experiment", equalTo("exp1"))
                .withQueryParam("experiment", equalTo("exp2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.experimentScheduleTools.getExperimentSchedules(experiments, null);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments/schedules/v2"))
                .withQueryParam("experiment", equalTo("exp1"))
                .withQueryParam("experiment", equalTo("exp2")));
    }

    @Test
    void getExperimentSchedulesWithTeamFilter() {
        // Given
        var expectedResponse = "TEST_RESPONSE";
        var teams = List.of("team1", "team2");

        wireMock.stubFor(get(urlPathEqualTo("/experiments/schedules/v2"))
                .withQueryParam("team", equalTo("team1"))
                .withQueryParam("team", equalTo("team2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.experimentScheduleTools.getExperimentSchedules(null, teams);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments/schedules/v2"))
                .withQueryParam("team", equalTo("team1"))
                .withQueryParam("team", equalTo("team2")));
    }

    @Test
    void getExperimentSchedulesWithBothFilters() {
        // Given
        var expectedResponse = "TEST_RESPONSE";
        var experiments = List.of("exp1");
        var teams = List.of("team1");

        wireMock.stubFor(get(urlPathEqualTo("/experiments/schedules/v2"))
                .withQueryParam("experiment", equalTo("exp1"))
                .withQueryParam("team", equalTo("team1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.experimentScheduleTools.getExperimentSchedules(experiments, teams);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments/schedules/v2"))
                .withQueryParam("experiment", equalTo("exp1"))
                .withQueryParam("team", equalTo("team1")));
    }

    @Test
    void getExperimentSchedulesWithEmptyFilters() {
        // Given
        var expectedResponse = "TEST_RESPONSE";
        List<String> emptyExperiments = List.of();
        List<String> emptyTeams = List.of();

        wireMock.stubFor(get(urlPathEqualTo("/experiments/schedules/v2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        // When
        var result = this.experimentScheduleTools.getExperimentSchedules(emptyExperiments, emptyTeams);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/experiments/schedules/v2")));
    }

}