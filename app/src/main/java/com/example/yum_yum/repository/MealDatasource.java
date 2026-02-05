package com.example.yum_yum.repository;

import com.example.yum_yum.data.meals.dto.MealResponse;

import io.reactivex.rxjava3.core.Single;

public interface MealDatasource {
    Single<MealResponse> getRandomMeal();

    // Corresponds to list.php?a=list
    Single<MealResponse> getAreaList();

    // Corresponds to filter.php?a=Canadian
    Single<MealResponse> getMealsByArea(String area);
}
