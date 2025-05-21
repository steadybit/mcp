/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steadybit.mcp.tools.ExperimentTools;
import com.steadybit.mcp.utils.SteadybitApiResponseHandler;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableConfigurationProperties(ApiProperties.class)
public class SteadybitMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteadybitMcpServerApplication.class, args);
    }

    @Bean
    public RestClient restClient(BuildProperties buildProperties, ApiProperties apiProperties, SteadybitApiResponseHandler steadybitApiResponseHandler) {
        return RestClient.builder()
                .baseUrl(apiProperties.getUrl())
                .defaultStatusHandler(steadybitApiResponseHandler)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "steadybit-mcp/" + buildProperties.getVersion())
                .defaultHeader("Authorization", "accessToken " + apiProperties.getToken())
                .build();
    }

    @Bean
    public SteadybitApiResponseHandler steadybitApiErrorHandler(ObjectMapper objectMapper) {
        return new SteadybitApiResponseHandler(objectMapper);
    }

    @Bean
    public ExperimentTools experimentService(RestClient restClient) {
        return new ExperimentTools(restClient);
    }

    @Bean
    public ToolCallbackProvider weatherTools(ExperimentTools experimentTools) {
        return MethodToolCallbackProvider.builder().toolObjects(experimentTools).build();
    }
}
