/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.steadybit.mcp.Tools;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.web.client.RestClient;

public class TeamTools implements Tools {

    private final RestClient restClient;

    public TeamTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_teams", description = """
            Get a list of teams, including their name, their allowed actions and environments, and their members
            
            # Steadybit Teams
            
            ## Overview
            A Steadybit team is a logical grouping of users that share common characteristics or are part of the same operational context. Users can be assigned to multiple teams. 
            - A team will have access to one or more environments. (can be fetched using the `list_environments` tool)
            - A team has permissions to execute specified actions. (can be fetched using the `list_actions` tool)
            """)
    public String getEnvironments() {
        return this.restClient.get()
                .uri("/teams")
                .retrieve()
                .body(String.class);
    }
}
