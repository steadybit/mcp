/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionTools {

    private final RestClient restClient;

    public ExecutionTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_experiment_executions", description = "Get a list of experiment executions, sorted by creation date")
    public String getExperimentExecutions(
            @ToolParam(description = "Filter by one or more experiment keys", required = false) List<String> experiment,
            @ToolParam(description = "Filter by one or more environment names", required = false) List<String> environment,
            @ToolParam(description = "Filter by one or more team keys", required = false) List<String> team,
            @ToolParam(description = "Filter by one or more result states. Possible values are [CREATED, PREPARED, RUNNING, FAILED, CANCELED, COMPLETED, ERRORED]", required = false) List<String> state,
            @ToolParam(description = "Filter by ISO8601 creation date from, example: 2025-05-21T13:00:00.000Z", required = false) Instant from,
            @ToolParam(description = "Filter by ISO8601 creation date to, example: 2025-05-21T15:00:00.000Z", required = false) Instant to,
            @ToolParam(description = "Number of the requested page, default is 0", required = false) Integer page,
            @ToolParam(description = "Results per page, defaults to 50, maximum 100 is allowed", required = false) Integer pageSize
    ) {
        Map<String, Object> body = new HashMap<>();
        if (experiment != null && !experiment.isEmpty()) {
            body.put("experimentKeys", experiment);
        }
        if (environment != null && !environment.isEmpty()) {
            body.put("environments", environment);
        }
        if (team != null && !team.isEmpty()) {
            body.put("teamKeys", team);
        }
        if (state != null && !state.isEmpty()) {
            body.put("states", state);
        }
        if (from != null) {
            body.put("requestedFrom", from);
        }
        if (to != null) {
            body.put("requestedTo", to);
        }
        body.put("page", page != null ? page : 0);
        body.put("size", pageSize != null ? pageSize : 50);
        return this.restClient.post()
                .uri("/experiments/executions")
                .body(body)
                .retrieve()
                .body(String.class);
    }

    @Tool(name = "get_experiment_execution", description = "Get an experiment execution")
    public String getExperimentExecution(@ToolParam(description = "The numeric id of the experiment execution") Integer executionId) {
        return this.restClient.get()
                .uri("/experiments/executions/" + executionId + "?fields=steps")
                .retrieve()
                .body(String.class);
    }

}
