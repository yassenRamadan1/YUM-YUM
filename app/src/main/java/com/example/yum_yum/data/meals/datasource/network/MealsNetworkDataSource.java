package com.example.yum_yum.data.meals.datasource.network;

import com.example.yum_yum.data.meals.dto.AreaResponse;
import com.example.yum_yum.data.meals.dto.MealResponse;
import com.example.yum_yum.data.network.MealService;
import com.example.yum_yum.data.network.NetworkClient;

import io.reactivex.rxjava3.core.Single;

public class MealsNetworkDataSource {
    private final MealService mealService = NetworkClient.getInstance().create(MealService.class);

    public Single<MealResponse> getRandomMeal() {
        return mealService.getRandomMeal();
    }

    public Single<AreaResponse> getAllAreas() {
        return mealService.getAllAreas();
    }

    public Single<MealResponse> getMealsByArea(String area) {
        return mealService.filterByArea(area);
    }

    public Single<MealResponse> getMealById(String mealId) {
        return mealService.getMealById(mealId);
    }
}
