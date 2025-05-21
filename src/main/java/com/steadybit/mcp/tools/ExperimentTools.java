/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.steadybit.api.model.BaseExperimentStepAO;
import com.steadybit.api.model.ExperimentAO;
import com.steadybit.api.model.ExperimentLaneAO;
import com.steadybit.api.model.ExperimentSummariesAO;
import com.steadybit.mcp.utils.SteadybitApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

import java.util.HashSet;
import java.util.Set;

public class ExperimentTools {

    private static final Logger log = LoggerFactory.getLogger(ExperimentTools.class);

    private final RestClient restClient;

    public ExperimentTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_experiment_designs", description = "Get a list of experiments designed in Steadybit")
    public String getExperimentDesigns(@ToolParam(description = "The key of a team") String team) {
        try {
            var result = this.restClient.get()
                    .uri(uri -> {
                        uri.path("/experiments");
                        if (team != null) {
                            uri.queryParam("team", team);
                        }
                        return uri.build();
                    })
                    .retrieve()
                    .body(ExperimentSummariesAO.class);

            if (result == null || result.getExperiments() == null || result.getExperiments().isEmpty()) {
                return "No experiments found";
            } else {
                StringBuilder sb = new StringBuilder();
                for (var experiment : result.getExperiments()) {
                    sb.append("Experiment Key: ").append(experiment.getKey()).append("\n");
                    sb.append("Name: ").append(experiment.getName()).append("\n");
                    sb.append("\n");
                }
                return sb.toString();
            }
        } catch (SteadybitApiException e) {
            return e.getMessage();
        } catch (Exception e) {
            log.error("Error while getting experiment design summary", e);
            return "There seems to be an error with the request. Please check the logs for more details.";
        }
    }

    @Tool(name = "get_experiment_design_summary", description = "Get a summary of an experiment design in Steadybit")
    public String getExperimentDesignSummary(@ToolParam(description = "The key of the experiment design") String experimentKey) {
        try {
            var result = this.restClient.get()
                    .uri("/experiments/" + experimentKey)
                    .retrieve()
                    .body(ExperimentAO.class);

            if (result != null) {
                Set<String> actions = new HashSet<>();
                for (ExperimentLaneAO lane : result.getLanes()) {
                    for (BaseExperimentStepAO step : lane.getSteps()) {
                        if (step.getActionType() != null) {
                            actions.add(step.getActionType());
                        }
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Experiment Key: ").append(result.getKey()).append("\n");
                sb.append("Name: ").append(result.getName()).append("\n");
                if (result.getHypothesis() != null) {
                    sb.append("Hypothesis: ").append(result.getHypothesis()).append("\n");
                }
                sb.append("Environment: ").append(result.getEnvironment()).append("\n");
                if (!actions.isEmpty()) {
                    sb.append("Actions: \n");
                    for (String action : actions) {
                        sb.append("- ").append(action).append("\n");
                    }
                }
                sb.append("\n\n");
                sb.append("Created at: ").append(result.getCreated()).append(" by ").append(result.getCreatedBy().getName()).append("\n");
                sb.append("Edited at: ").append(result.getEdited()).append(" by ").append(result.getCreatedBy().getName()).append("\n");
                return sb.toString();
            } else {
                return "No experiment found";
            }
        } catch (SteadybitApiException e) {
            return e.getMessage();
        } catch (Exception e) {
            log.error("Error while getting experiment design summary", e);
            return "There seems to be an error with the request. Please check the logs for more details.";
        }
    }
}
