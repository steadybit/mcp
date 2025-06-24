/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.tools;

import com.steadybit.mcp.Tools;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

public class ExperimentTools implements Tools {

    private final RestClient restClient;

    public ExperimentTools(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(name = "list_experiment_designs", description = "Get a list of experiments designed")
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

    @Tool(name = "get_experiment_design", description = """
            Get an experiment design
            
            # Steadybit Experiment Design Format
            
            ## Overview
            The ExperimentAO schema defines the structure for chaos engineering experiments in Steadybit. An experiment is a structured test that simulates failures and validates system resilience through automated actions and checks.
            
            ## Core Structure
            ### Top-Level Properties
            ```json
            {
              "name": "string",           // Human-readable experiment name
              "key": "string",            // Unique identifier for the experiment, combination of team key followed by a number (e.g., "ADM-5")
              "team": "string",           // Team key (e.g., "ADM")
              "hypothesis": "string",     // Hypothesis or goal of the experiment (e.g., "Shop survives unavailability of hot-deals products")
              "environment": "string",    // Environment name (e.g., "Global", "Production")
              "lanes": [...],             // Array of parallel execution lanes
            }
            ```
            
            ## Experiment Architecture
            ### **Lanes Structure**
            Experiments are organized into **lanes** - parallel execution tracks that can run simultaneously. Each lane contains a sequence of **steps** that execute sequentially within that lane.
            ```json
            {
              "lanes": [
                {
                  "steps": [
                    // Sequential steps within this lane
                  ]
                },
                {
                  "steps": [
                    // Another parallel lane with its own steps
                  ]
                }
              ]
            }
            ```
            ### **Step Types**
            Each step in a lane can be one of two types:
            #### 1. **Action Steps** (Chaos Engineering Actions)
            ```json
            {
              "type": "action",
              "ignoreFailure": false,                    // Whether to continue if this step fails
              "actionType": "string",                    // Specific action identifier
              "customLabel": "string",                   // Optional human-readable description
              "parameters": {...},                       // Action-specific configuration, parameters are defined by in the action definition and can be fetched by the "list_actions" tool
              "radius": {                                // Actions that need a target define the target selection and scope
                "targetType": "string",                  // Type of target to use (e.g., "com.steadybit.extension_container.container")
                "predicate": {...},                      // Target selection criteria
                "query": "string",                       // Alternative for "predicate", optional query language filter
                "percentage": 100,                       // Percentage of the targets resulting from the given "predicate" to affect
                "maximum": 1                             // Alternative for "percentage", fixed number of targets to affect
              }
            }
            ```
            #### 2. **Wait Steps** (Delays)
            ```json
            {
              "type": "wait",
              "customLabel": "string",                  // Optional human-readable description
              "parameters": {
                "duration": "10s"                       // Duration to wait (ISO 8601 format)
              }
            }
            ```
            
            ## Target Selection
            The **radius** object defines which targets an action affects. Targets are required for actions that specify a `target` in their definition.
            ### **Target Types** (Common Examples)
            - `com.steadybit.extension_container.container` - Kubernetes containers
            - `com.steadybit.extension_host.host` - Physical/virtual hosts
            - `com.steadybit.extension_kubernetes.kubernetes-deployment` - K8s deployments
            - `com.steadybit.extension_aws.ec2-instance` - AWS EC2 instances
            Available target types depends on installed extensions and can be fetched by the "get_target_stats" tool.
            ### **Predicate Structure** (Target Filtering)
            ```json
            {
              "predicate": {
                "operator": "AND",                      // Logical operator: AND, OR
                "predicates": [
                  {
                    "key": "k8s.container.name",        // Attribute to filter on
                    "operator": "EQUALS",               // Comparison operator
                    "values": ["hot-deals"]             // Values to match
                  },
                  {
                    "key": "k8s.namespace",
                    "operator": "EQUALS",
                    "values": ["production"]
                  }
                ]
              }
            }
            ```
            ### **Blast Radius Control**
            - only required for actions that has no quantity restriction and for actions that require a target
            - `percentage`: Limit what percentage of matching targets are affected
            - `maximum`: Alternative to `percentage`, set fixed number of targets
            - Prevents accidentally affecting too many resources
            
            ## Action Types and Parameters
            ### **Action Types** (Common Examples)
            - `com.steadybit.extension_http.check.periodically` - Periodic HTTP checks
            - `com.steadybit.extension_container.network_blackhole` - Simulate network blackhole for containers
            - `com.steadybit.extension_container.stress_cpu` - Stress CPU of containers
            - `com.steadybit.extension_container.stress_memory` - Stress memory of containers
            - `com.steadybit.extension_kubernetes.delete_pod` - Delete a pod in Kubernetes
            Available actions depends on installed extensions and can be fetched by the "list_actions" tool. The action definitions will also provide the available parameters.
            
            ## Execution Flow
            ### **Sequential vs Parallel Execution**
            1. **Within a Lane**: Steps execute sequentially (one after another)
            2. **Across Lanes**: Lanes execute in parallel (simultaneously)
            3. **Timing**: The experiment duration is determined by the longest-running lane
            """)
    public String getExperimentDesign(@ToolParam(description = "The key of the experiment design") String experimentKey) {
        return this.restClient.get()
                .uri("/experiments/" + experimentKey)
                .retrieve()
                .body(String.class);
    }
}
