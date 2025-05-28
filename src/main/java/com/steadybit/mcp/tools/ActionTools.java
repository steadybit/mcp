/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.web.client.RestClient;

public class ActionTools {

    private final RestClient restClient;

    public ActionTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_actions", description = "Get a list of of all actions that are currently registered.")
    public String getActions() {
        return this.restClient.get()
                .uri("/actions")
                .retrieve()
                .body(String.class);
    }

}
