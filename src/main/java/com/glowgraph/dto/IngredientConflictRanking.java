package com.glowgraph.dto;

public class IngredientConflictRanking {

    private String ingredient;
    private Long conflictCount;

    public IngredientConflictRanking(String ingredient, Long conflictCount) {
        this.ingredient = ingredient;
        this.conflictCount = conflictCount;
    }

    public String getIngredient() {
        return ingredient;
    }

    public Long getConflictCount() {
        return conflictCount;
    }
}