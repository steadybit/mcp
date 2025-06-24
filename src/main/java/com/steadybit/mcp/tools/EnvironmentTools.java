/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.steadybit.mcp.Tools;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.web.client.RestClient;

public class EnvironmentTools implements Tools {

    private final RestClient restClient;

    public EnvironmentTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_environments", description = """
            Get a list of environments, including their name and target selection criteria.
            
            # Steadybit Environments
            
            ## Overview
            A Steadybit environment is a logical grouping of targets that share common characteristics or are part of the same operational context.
            
            ## Target Selection Criteria
            The target selection criteria for environments are defined in either the `predicate` or the `query` field of the environment object. These criteria specify how targets are selected for the environment.
            - `predicate`: A JSON object that defines the selection criteria in a structured format.
            - `query`: A string that represents the selection criteria in a more human-readable format.
            If both fields are present, the `query` field takes precedence over the `predicate` field.
            """)
    public String getEnvironments() {
        return this.restClient.get()
                .uri("/environments")
                .retrieve()
                .body(String.class);
    }
}
