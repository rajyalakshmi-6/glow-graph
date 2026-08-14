package com.glowgraph.dto;

public class ConflictReasonInfo {

    private String ingredient1;
    private String ingredient2;
    private String reason;
    private String severity;

    public ConflictReasonInfo(
            String ingredient1,
            String ingredient2,
            String reason,
            String severity
    ) {
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.reason = reason;
        this.severity = severity;
    }

    public String getIngredient1() {
        return ingredient1;
    }

    public String getIngredient2() {
        return ingredient2;
    }

    public String getReason() {
        return reason;
    }

    public String getSeverity() {
        return severity;
    }
}