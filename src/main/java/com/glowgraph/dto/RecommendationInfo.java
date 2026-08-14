package com.glowgraph.dto;

public class RecommendationInfo {

    private String productName;
    private String brand;
    private String category;
    private Double price;
    private String imageUrl;
    private String ingredient;
    private String concern;

    public RecommendationInfo(
            String productName,
            String brand,
            String category,
            Double price,
            String imageUrl,
            String ingredient,
            String concern
    ) {
        this.productName = productName;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.ingredient = ingredient;
        this.concern = concern;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getConcern() {
        return concern;
    }
}