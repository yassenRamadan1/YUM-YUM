package com.example.yum_yum.presentation.model;

public class IngredientItem {
    private final String name;
    private final String measure;

    public IngredientItem(String name, String measure) {
        this.name = name;
        this.measure = measure;
    }

    public String getDisplayText() {
        return measure + " " + name; // e.g., "1/4 cup olive oil"
    }
}