/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "capabilities")
@lombok.Data
public class CapabilitiesProperties {

    private static final Logger log = LoggerFactory.getLogger(Capabilities.class);

    private List<Capabilities> enabled;

    @PostConstruct
    public void logCapabilities() {
        log.warn("Capabilities enabled: {}", this.enabled);
    }

}
