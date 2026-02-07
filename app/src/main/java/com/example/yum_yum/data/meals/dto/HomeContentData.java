package com.example.yum_yum.data.meals.dto;

import com.example.yum_yum.presentation.model.Meal;

import java.io.Serializable;
import java.util.List;

public class HomeContentData {
    private final Meal dailyMeal;
    private final List<Meal> countryMeals;
    private final String countryName;

    public HomeContentData(Meal dailyMeal, List<Meal> countryMeals, String countryName) {
        this.dailyMeal = dailyMeal;
        this.countryMeals = countryMeals;
        this.countryName = countryName;
    }

    public Meal getDailyMeal() {
        return dailyMeal;
    }

    public List<Meal> getCountryMeals() {
        return countryMeals;
    }

    public String getCountryName() {
        return countryName;
    }
}

