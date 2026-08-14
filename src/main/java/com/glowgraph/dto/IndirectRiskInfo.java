package com.glowgraph.dto;

import java.util.List;

public class IndirectRiskInfo {

    private String sourceIngredient;
    private String targetIngredient;
    private Long pathLength;
    private List<String> conflictPath;

    public IndirectRiskInfo(
            String sourceIngredient,
            String targetIngredient,
            Long pathLength,
            List<String> conflictPath
    ) {
        this.sourceIngredient = sourceIngredient;
        this.targetIngredient = targetIngredient;
        this.pathLength = pathLength;
        this.conflictPath = conflictPath;
    }

    public String getSourceIngredient() {
        return sourceIngredient;
    }

    public String getTargetIngredient() {
        return targetIngredient;
    }

    public Long getPathLength() {
        return pathLength;
    }

    public List<String> getConflictPath() {
        return conflictPath;
    }
}