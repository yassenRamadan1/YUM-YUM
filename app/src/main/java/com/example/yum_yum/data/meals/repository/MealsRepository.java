package com.example.yum_yum.data.meals.repository;

import static com.example.yum_yum.data.meals.repository.MealMapper.mapToUiModel;

import com.example.yum_yum.data.meals.datasource.network.MealsNetworkDataSource;
import com.example.yum_yum.presentation.model.Meal;

import io.reactivex.rxjava3.core.Single;

public class MealsRepository {
    private final MealsNetworkDataSource dataSource = new MealsNetworkDataSource();

    public Single<Meal> getRandomMeal(){
        Single<Meal> randomMeal =  dataSource.getRandomMeal().map(
                meal -> mapToUiModel(meal.getMeals().get(0))
        );
        return randomMeal;
    }


}