package com.example.yum_yum.data.meals.dto;

import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public  class CountryMealsData {
    String countryName;
    List<Meal> meals;
    public CountryMealsData(String countryName, List<Meal> meals) {
        this.countryName = countryName;
        this.meals = meals;
    }

    public String getCountryName() {
        return countryName;
    }

    public List<Meal> getMeals() {
        return meals;
    }
}
