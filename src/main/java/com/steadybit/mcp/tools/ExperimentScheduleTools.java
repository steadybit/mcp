/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.steadybit.mcp.Tools;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ExperimentScheduleTools implements Tools {

    private final RestClient restClient;

    public ExperimentScheduleTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_experiment_schedules", description = """
            Get a list of experiment schedules
            
            # Steadybit Experiment Schedules
            
            ## Overview
            A steadybit experiment schedule is a configuration that defines scheduled experiment executions.
            
            ## Execution time
            The execution time of an experiment schedule is either defined using a `cron` expression or a single execution time `startAt` and a `timezone` field.
            """)
    public String getExperimentSchedules(
            @ToolParam(description = "Filter by one or more experiment keys", required = false) List<String> experiment,
            @ToolParam(description = "Filter by one or more team keys", required = false) List<String> team
    ) {
        return this.restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/experiments/schedules/v2");
                    if (experiment != null && !experiment.isEmpty()) {
                        uriBuilder.queryParam("experiment", experiment);
                    }
                    if (team != null && !team.isEmpty()) {
                        uriBuilder.queryParam("team", team);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(String.class);
    }
}
