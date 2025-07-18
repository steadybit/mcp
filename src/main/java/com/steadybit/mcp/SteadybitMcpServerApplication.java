/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp;

import com.steadybit.mcp.tools.*;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableConfigurationProperties({ApiProperties.class, CapabilitiesProperties.class})
public class SteadybitMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteadybitMcpServerApplication.class, args);
    }

    @Bean
    public RestClient restClient(BuildProperties buildProperties, ApiProperties apiProperties) {
        return RestClient.builder()
                .baseUrl(apiProperties.getUrl())
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "steadybit-mcp/" + buildProperties.getVersion())
                .defaultHeader("Authorization", "accessToken " + apiProperties.getToken())
                .build();
    }

    @Bean
    public ExperimentTools experimentService(RestClient restClient) {
        return new ExperimentTools(restClient);
    }

    @Bean
    public ExecutionTools executionTools(RestClient restClient) {
        return new ExecutionTools(restClient);
    }

    @Bean
    public ActionTools actionTools(RestClient restClient) {
        return new ActionTools(restClient);
    }

    @Bean
    public EnvironmentTools environmentTools(RestClient restClient) {
        return new EnvironmentTools(restClient);
    }

    @Bean
    public TeamTools teamTools(RestClient restClient) {
        return new TeamTools(restClient);
    }

    @Bean
    public ExperimentScheduleTools experimentScheduleTools(RestClient restClient) {
        return new ExperimentScheduleTools(restClient);
    }

    @Bean
    public ExperimentTemplateTools experimentTemplateTools(RestClient restClient, CapabilitiesProperties capabilitiesProperties) {
        return new ExperimentTemplateTools(restClient, capabilitiesProperties.getEnabled());
    }

    @Bean
    public ToolCallbackProvider tools(List<Tools> tools) {
        return MethodToolCallbackProvider.builder().toolObjects((Object[]) tools.toArray(new Tools[0])).build();
    }
}
