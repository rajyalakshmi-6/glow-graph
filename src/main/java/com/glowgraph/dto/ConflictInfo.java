package com.glowgraph.dto;

public class ConflictInfo {

    private String product1;
    private String ingredient1;
    private String ingredient2;
    private String product2;
    private String reason;
    private String severity;

    public ConflictInfo(
            String product1,
            String ingredient1,
            String ingredient2,
            String product2,
            String reason,
            String severity
    ) {
        this.product1 = product1;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.product2 = product2;
        this.reason = reason;
        this.severity = severity;
    }

    public String getProduct1() {
        return product1;
    }

    public String getIngredient1() {
        return ingredient1;
    }

    public String getIngredient2() {
        return ingredient2;
    }

    public String getProduct2() {
        return product2;
    }

    public String getReason() {
        return reason;
    }

    public String getSeverity() {
        return severity;
    }
}