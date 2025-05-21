/*
 * Copyright 2025 steadybit GmbH. All rights reserved.
 */

package com.steadybit.mcp.utils;

import java.util.List;

@lombok.Data
public class ProblemAO {
    private String instance;
    private String type;
    private String detail;
    private int status;
    private String title;
    private List<ViolationAO> violations;

    @lombok.Data
    public static class ViolationAO {
        private String message;
        private String field;
    }

}
