/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.web.client.RestClient;

public class ApiTools {

    private final RestClient restClient;

    public ApiTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "get_api_spec", description = "Get the OpenApi specification from Steadybit. The API may be used to create experiments, start experiment executions, managing teams or environment or many more.")
    public String getApiSpec() {
        return this.restClient.get()
                .uri("/spec/platform")
                .retrieve()
                .body(String.class);
    }

}
