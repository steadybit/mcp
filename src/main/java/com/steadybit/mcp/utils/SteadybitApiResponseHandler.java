/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class SteadybitApiResponseHandler implements ResponseErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(SteadybitApiResponseHandler.class);

    private final ObjectMapper objectMapper;

    public SteadybitApiResponseHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        log.info("HTTP Status Code: " + response.getStatusCode() + " for " + method + " @ " + url);
        if (response.getStatusCode().value() == 401) {
            throw new SteadybitApiException("Unauthorized, please check your token.");
        }
        if (response.getStatusCode().value() == 404) {
            throw new SteadybitApiException("The item could not be found.");
        }
        if (response.getStatusCode().value() == 422) {
            ProblemAO problemAO;
            try {
                problemAO = this.objectMapper.readValue(response.getBody(), ProblemAO.class);
            } catch (Exception e) {
                throw new SteadybitApiException("Invalid request.");
            }

            if (problemAO == null) {
                throw new SteadybitApiException("Invalid request.");
            } else if (problemAO.getViolations() != null) {
                StringBuilder sb = new StringBuilder();
                for (var violation : problemAO.getViolations()) {
                    sb.append(violation.getField()).append(": ").append(violation.getMessage()).append("\n");
                }
                throw new SteadybitApiException(sb.toString());
            } else if (problemAO.getTitle() != null) {
                throw new SteadybitApiException(problemAO.getTitle());
            } else {
                throw new SteadybitApiException("Invalid request.");
            }
        }
    }
}
