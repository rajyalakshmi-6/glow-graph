package com.glowgraph.dto;

public class SynergyInfo {

    private String ingredient1;
    private String ingredient2;
    private String benefit;

    public SynergyInfo(
            String ingredient1,
            String ingredient2,
            String benefit
    ) {
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.benefit = benefit;
    }

    public String getIngredient1() {
        return ingredient1;
    }

    public String getIngredient2() {
        return ingredient2;
    }

    public String getBenefit() {
        return benefit;
    }
}