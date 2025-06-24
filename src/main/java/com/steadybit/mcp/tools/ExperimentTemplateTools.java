/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.steadybit.mcp.Capabilities;
import com.steadybit.mcp.Tools;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.steadybit.mcp.Capabilities.CREATE_EXPERIMENT_FROM_TEMPLATE;

public class ExperimentTemplateTools implements Tools {

    private final RestClient restClient;
    private final List<Capabilities> capabilities;

    public ExperimentTemplateTools(RestClient restClient, List<Capabilities> capabilities) {
        this.restClient = restClient;
        this.capabilities = capabilities;
    }

    @Tool(name = "list_experiment_templates", description = """
            Get a list of experiment Templates, including their name and id. The content of the templates is not included in the response. Use the `get_experiment_template` tool to retrieve the content of a specific template.
            
            # Steadybit Experiment Templates
            
            ## Overview
            A steadybit experiment template is a reusable definition of an experiment that can be used to create new experiments. It contains all the necessary information to run an experiment, including the experiment type, parameters, and execution schedule. It may contain placeholders that needs to be replaced with actual values when the experiment is created.
            """)
    public String getExperimentTemplates() {
        return this.restClient.get()
                .uri("/experiments/templates")
                .retrieve()
                .body(String.class);
    }

    @Tool(name = "get_experiment_template", description = """
            Get a specific experiment template by its ID. The response includes the content of the template, which contains all the necessary information to run an experiment, including the experiment type, parameters, and execution schedule. It may contain placeholders that needs to be replaced with actual values when the experiment is created.
            """)
    public String getExperimentTemplate(
            @ToolParam(description = "The id of the experiment template (UUID)") UUID templateId
    ) {
        return this.restClient.get()
                .uri("/experiments/templates/" + templateId)
                .retrieve()
                .body(String.class);
    }

    @Tool(name = "create_experiment_from_template", description = """
            Create a new experiment from an experiment template. The response will include the experiment key of the newly created experiment or an error message if the creation failed.
            
            Placeholders defined in the template must be provided by the user. The placeholders are specified as a map of key-value pairs, where the key is the placeholder name and the value is the value to replace the placeholder with.
            """)
    public String createExperimentFromTemplate(
            @ToolParam(description = "The id of the experiment template (UUID)") UUID templateId,
            @ToolParam(description = "The name of the environment to use") String environment,
            @ToolParam(description = "The key of the team to use") String team,
            @ToolParam(description = "A map of placeholder keys and their values. All placeholders specified by the template needs to be provided by a user.", required = false) Map<String, String> placeholders,
            @ToolParam(description = "An optional external id that can be used to update existing experiment designs", required = false) String externalId
    ) {
        if (!this.capabilities.contains(CREATE_EXPERIMENT_FROM_TEMPLATE)) {
            return "Not allowed. You need to enable the CREATE_EXPERIMENT_FROM_TEMPLATE capability in the MCP configuration. For example: `CAPABILITIES_ENABLED_0=CREATE_EXPERIMENT_FROM_TEMPLATE`";
        }
        Map<String, Object> body = new HashMap<>();
        body.put("environment", environment);
        body.put("team", team);
        if (placeholders != null && !placeholders.isEmpty()) {
            body.put("placeholders", placeholders.entrySet().stream()
                    .map(entry -> Map.of("key", entry.getKey(), "value", entry.getValue()))
                    .toList());
        }
        if (externalId != null) {
            body.put("externalId", externalId);
        }

        ResponseEntity<String> response = this.restClient.post()
                .uri("/experiments/templates/" + templateId + "/experiment-create")
                .body(body)
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        String location = response.getHeaders().getFirst("Location");
        return "Created Experiment: " + location;
    }
}
