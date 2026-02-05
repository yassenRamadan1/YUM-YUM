package com.example.yum_yum.data.meals.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealResponse {
    @SerializedName("meals")
    private List<MealDto> meals;

    public List<MealDto> getMeals() { return meals; }
}
