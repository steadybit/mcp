/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.steadybit.mcp.Tools;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

public class ActionTools implements Tools {

    private final RestClient restClient;

    public ActionTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_actions", description = "Get a list of of all actions that are currently registered.")
    public String getActions(
            @ToolParam(description = "Number of the requested page, default is 0", required = false) Integer page,
            @ToolParam(description = "Results per page, defaults to 50, maximum 100 is allowed", required = false) Integer pageSize
    ) {
        return this.restClient.get()
                .uri(uri -> {
                    uri.path("/actions");
                    if (page != null) {
                        uri.queryParam("page", page);
                    }
                    if (pageSize != null) {
                        uri.queryParam("size", pageSize);
                    }
                    return uri.build();
                })
                .retrieve()
                .body(String.class);
    }

}
