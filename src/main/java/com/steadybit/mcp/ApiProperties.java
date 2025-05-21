/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
@lombok.Data
public class ApiProperties {
    private String url = "https://platform.steadybit.com/api";
    private String token;

    @PostConstruct
    public void validate() {
        //using System.err because logging isn't writing anything to console
        if (this.url == null || this.url.isBlank()) {
            System.err.println("Property 'api.url' must be set");
            throw new IllegalArgumentException("Property 'api.url' must be set");
        }
        if (this.token == null || this.token.isBlank()) {
            System.err.println("Property 'api.token' must be set");
            throw new IllegalArgumentException("Property 'api.token' must be set");
        }
    }


}
