package com.example.yum_yum.presentation.model;

import java.io.Serializable;

public class IngredientItem implements Serializable {
    public String getName() {
        return name;
    }

    public String getMeasure() {
        return measure;
    }

    private final String name;
    private final String measure;

    public IngredientItem(String name, String measure) {
        this.name = name;
        this.measure = measure;
    }

    public String getDisplayText() {
        return measure + " " + name;
    }
    public String getImageUrl() {
        return "https://www.themealdb.com/images/ingredients/" + name + ".png";
    }
}