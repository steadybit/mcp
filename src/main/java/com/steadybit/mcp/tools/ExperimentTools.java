/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

public class ExperimentTools {

    private final RestClient restClient;

    public ExperimentTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_experiment_designs", description = "Get a list of experiments designed in Steadybit")
    public String getExperimentDesigns(@ToolParam(description = "The key of a team") String team) {
        return this.restClient.get()
                .uri(uri -> {
                    uri.path("/experiments");
                    if (team != null) {
                        uri.queryParam("team", team);
                    }
                    return uri.build();
                })
                .retrieve()
                .body(String.class);
    }

    @Tool(name = "get_experiment_design_summary", description = "Get a summary of an experiment design in Steadybit")
    public String getExperimentDesignSummary(@ToolParam(description = "The key of the experiment design") String experimentKey) {
        return this.restClient.get()
                .uri("/experiments/" + experimentKey)
                .retrieve()
                .body(String.class);
    }
}
