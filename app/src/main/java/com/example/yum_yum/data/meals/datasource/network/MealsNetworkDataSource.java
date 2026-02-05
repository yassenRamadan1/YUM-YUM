package com.example.yum_yum.data.meals.datasource.network;

import com.example.yum_yum.data.meals.dto.MealResponse;
import com.example.yum_yum.data.network.MealService;
import com.example.yum_yum.data.network.NetworkClient;

import io.reactivex.rxjava3.core.Single;

public class MealsNetworkDataSource {
        private MealService mealService = NetworkClient.getInstance().create(MealService.class);

        public Single<MealResponse> getRandomMeal() {
            return mealService.getRandomMeal();
        }
}
