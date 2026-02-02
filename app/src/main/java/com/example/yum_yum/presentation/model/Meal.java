package com.example.yum_yum.presentation.model;

import java.util.List;

public class Meal {
    private final String id;
    private final String name;
    private final String imageUrl;
    private final String instructions;
    private final String category;
    private final String area;
    private final String youtubeUrl;
    private final List<IngredientItem> ingredients;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }


    public Meal(String id, String name, String imageUrl, String instructions, String category, String area, String youtubeUrl, List<IngredientItem> ingredients) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.instructions = instructions;
        this.category = category;
        this.area = area;
        this.youtubeUrl = youtubeUrl;
        this.ingredients = ingredients;
    }

    public List<IngredientItem> getIngredients() { return ingredients; }

}

